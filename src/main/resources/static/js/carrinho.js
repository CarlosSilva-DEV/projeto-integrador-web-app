// DEBUG
console.log('carrinho.js carregado - verificando elementos do DOM...');
console.log('cartItems:', document.getElementById('cartItems'));
console.log('emptyCart:', document.getElementById('emptyCart'));
console.log('summaryTotals:', document.getElementById('summaryTotals'));

let allProducts = [];
let cartItems = [];
let currentOrder = null;

// Inicializar a página
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    initializePage();
    setupEventListeners();
});

// Verificar se todos os elementos do DOM estão carregados
function ensureDOMElements() {
    const requiredElements = [
        'cartItems',
        'summaryTotals', 
        'emptyCart',
        'subtotal',
        'total',
        'productsGrid',
        'productsLoading'
    ];

    const missingElements = requiredElements.filter(id => !document.getElementById(id));
    
    if (missingElements.length > 0) {
        console.error('Elementos do DOM não encontrados:', missingElements);
        return false;
    }
    
    return true;
}

function checkRequiredElements() {
    const requiredElements = [
        'cartItems',
        'summaryTotals', 
        'emptyCart',
        'subtotal',
        'total',
        'productsGrid',
        'productsLoading',
        'checkoutBtn'
    ];

    const missingElements = requiredElements.filter(id => !document.getElementById(id));
    
    if (missingElements.length > 0) {
        console.error('Elementos obrigatórios não encontrados:', missingElements);
        return false;
    }
    
    console.log('Todos os elementos obrigatórios encontrados');
    return true;
}

async function initializePage() {
    checkAuthentication();
    
    // Verificar e sincronizar autenticação
    const authValid = await verifyAndSyncAuth();
    if (!authValid) {
        return;
    }
    
    checkAuthStatus();
    
    showProductsLoading();
    await updateNavigation();
    await loadProducts();
    
    // GARANTIR que os elementos do DOM estão carregados antes de inicializar o carrinho
    await ensureCartElements();
    
    loadCartFromStorage();
    hideProductsLoading();
    
    console.log('Página do carrinho inicializada com sucesso');
}

// Verificar autenticação
function checkAuthentication() {
    if (!authSystem.isLoggedIn()) {
        alert('Você precisa estar logado para acessar o carrinho!');
        window.location.href = 'login.html';
        return;
    }
}

// Verificar estado da autenticação
function checkAuthStatus() {
    console.log('=== VERIFICAÇÃO DE AUTENTICAÇÃO ===');
    console.log('Usuário logado:', authSystem.isLoggedIn());
    console.log('Token existe:', !!authSystem.getToken());
    console.log('Usuário atual:', authSystem.getCurrentUser());
    
    // Verificar se o token está expirado
    const token = authSystem.getToken();
    if (token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const exp = payload.exp * 1000; // Converter para milliseconds
            const now = Date.now();
            console.log('Token expira em:', new Date(exp).toLocaleString());
            console.log('Token expirado?', now > exp);
        } catch (e) {
            console.log('Erro ao decodificar token:', e);
        }
    }
    console.log('================================');
}

// Função para garantir que os elementos do carrinho existem
function ensureCartElements() {
    return new Promise((resolve) => {
        const checkElements = () => {
            const cartItems = document.getElementById('cartItems');
            const summaryTotals = document.getElementById('summaryTotals');
            const emptyCart = document.getElementById('emptyCart');
            
            if (cartItems && summaryTotals && emptyCart) {
                console.log('Elementos do carrinho encontrados');
                resolve(true);
            } else {
                console.log('Aguardando elementos do carrinho...');
                setTimeout(checkElements, 100);
            }
        };
        
        checkElements();
    });
}

// Atualizar navegação
async function updateNavigation() {
    const navButtons = document.getElementById('navButtons');
    const isLoggedIn = authSystem.isLoggedIn();

    if (isLoggedIn) {
        const currentUser = authSystem.getCurrentUser();
        const userName = currentUser ? currentUser.nome : 'Usuário';
        const initials = getUserInitials(userName);

        navButtons.innerHTML = `
            <a href="homepage.html" class="btn btn-outline">
                <i class="fas fa-arrow-left"></i> Continuar Comprando
            </a>
            <div class="user-menu">
                <span class="user-name">${userName}</span>
                <div class="user-avatar">${initials}</div>
                <div class="user-dropdown">
                    <a href="perfil.html"><i class="fas fa-user"></i> Meu Perfil</a>
                    <a href="#" onclick="authSystem.logout()"><i class="fas fa-sign-out-alt"></i> Sair</a>
                </div>
            </div>
        `;
    }
}

// Configurar event listeners
function setupEventListeners() {
    const searchInput = document.getElementById('productSearch');
    const searchBtn = document.getElementById('searchBtn');
    const checkoutBtn = document.getElementById('checkoutBtn');

    if (searchInput) {
        searchInput.addEventListener('input', debounce(handleSearch, 300));
    }
    
    if (searchBtn) {
        searchBtn.addEventListener('click', handleSearch);
    }
    
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', handleCheckout);
    }

    // Eventos do modal
    const continueShoppingBtn = document.getElementById('continueShoppingBtn');
    const goToPaymentBtn = document.getElementById('goToPaymentBtn');

    if (continueShoppingBtn) {
        continueShoppingBtn.addEventListener('click', function() {
            const modal = document.getElementById('confirmationModal');
            if (modal) modal.style.display = 'none';
            continueShopping();
        });
    }

    if (goToPaymentBtn) {
        goToPaymentBtn.addEventListener('click', function() {
            if (currentOrder) {
                window.location.href = `pagamento.html?orderId=${currentOrder.id}`;
            }
        });
    }
}

// Carregar produtos do backend
async function loadProducts(searchTerm = '') {
    try {
        let url = '/products'; // CORREÇÃO: Apenas endpoint
        if (searchTerm) {
            url += `?q=${encodeURIComponent(searchTerm)}`;
        }

        const response = await authSystem.authenticatedFetch(url);
        if (!response.ok) {
            throw new Error(`Erro HTTP! status: ${response.status}`);
        }

        allProducts = await response.json();
        renderProducts(allProducts);
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        showError('Erro ao carregar produtos: ' + error.message);
    }
}

// Renderizar produtos
function renderProducts(products) {
    const productsGrid = document.getElementById('productsGrid');
    
    if (!productsGrid) {
        console.error('Elemento productsGrid não encontrado');
        return;
    }

    if (products.length === 0) {
        productsGrid.innerHTML = '<div class="no-products">Nenhum produto encontrado.</div>';
        return;
    }

    const productsHTML = products.map(product => {
        const cartItem = cartItems.find(item => item.productId === product.id);
        const quantity = cartItem ? cartItem.quantity : 0;

        console.log(`Produto ${product.id}: quantidade no carrinho = ${quantity}`);

        return `
            <div class="product-card" data-product-id="${product.id}">
                <div class="product-image">
                    <img src="${product.imgUrl || 'https://via.placeholder.com/300x300/e9ecef/6c757d?text=Produto'}" 
                         alt="${product.nome}" 
                         onerror="this.src='https://via.placeholder.com/300x300/e9ecef/6c757d?text=Produto'">
                </div>
                <div class="product-info">
                    <div class="product-category">${getProductCategories(product)}</div>
                    <h3 class="product-name">${product.nome}</h3>
                    <div class="product-price">R$ ${product.preco.toFixed(2)}</div>
                    <div class="product-actions">
                        <div class="quantity-controls" ${quantity === 0 ? 'style="display: none;"' : ''}>
                            <button class="quantity-btn minus" onclick="updateProductQuantity(${product.id}, -1)" 
                                    ${quantity === 0 ? 'disabled' : ''}>-</button>
                            <span class="quantity">${quantity}</span>
                            <button class="quantity-btn plus" onclick="updateProductQuantity(${product.id}, 1)"
                                    ${quantity >= 10 ? 'disabled' : ''}>+</button>
                        </div>
                        <button class="btn-add-cart" onclick="addToCart(${product.id})"
                                ${quantity > 0 ? 'style="display: none;"' : ''}>
                            <i class="fas fa-cart-plus"></i> Adicionar
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');

    productsGrid.innerHTML = productsHTML;
    console.log('Produtos renderizados:', products.length);
}

// Buscar produto por ID (para quando não estiver na lista carregada)
async function fetchProductById(productId) {
    try {
        const response = await authSystem.authenticatedFetch(`/products/${productId}`);
        if (!response.ok) {
            throw new Error('Produto não encontrado');
        }
        return await response.json();
    } catch (error) {
        console.error('Erro ao buscar produto:', error);
        return null;
    }
}

// Adicionar produto ao carrinho
async function addToCart(productId) {
    try {
        console.log('Adicionando produto ao carrinho:', productId);
        
        let product = allProducts.find(p => p.id === productId);
        if (!product) {
            console.error('Produto não encontrado na lista local. ID:', productId);
            showTempMessage('Produto não encontrado!', 'error');
            return;
        }

        console.log('Produto encontrado:', product.nome);

        // Verificar se o produto já está no carrinho
        const existingItemIndex = cartItems.findIndex(item => item.productId === productId);
        
        if (existingItemIndex >= 0) {
            // Se já existe, incrementar quantidade
            if (cartItems[existingItemIndex].quantity < 10) {
                cartItems[existingItemIndex].quantity += 1;
                cartItems[existingItemIndex].subtotal = cartItems[existingItemIndex].quantity * cartItems[existingItemIndex].preco;
                showTempMessage('Quantidade aumentada!', 'info');
                console.log('Quantidade aumentada para:', cartItems[existingItemIndex].quantity);
            } else {
                showTempMessage('Quantidade máxima permitida é 10', 'error');
                return;
            }
        } else {
            // Se não existe, adicionar novo item
            const newItem = {
                productId: product.id,
                product: product,
                quantity: 1,
                preco: product.preco,
                subtotal: product.preco
            };
            cartItems.push(newItem);
            showTempMessage('Produto adicionado ao carrinho!', 'success');
            console.log('Novo item adicionado:', newItem);
        }

        saveCartToStorage();
        
        // Atualizar a exibição dos produtos (para mostrar controles de quantidade)
        renderProducts(allProducts);
        
        // Atualizar o carrinho
        updateCartDisplay();
        window.location.reload();
        
        console.log('Carrinho após adição:', cartItems);
        
    } catch (error) {
        console.error('Erro ao adicionar produto:', error);
        showTempMessage('Erro ao adicionar produto', 'error');
    }
}

// Atualizar quantidade do produto
function updateProductQuantity(productId, change) {
    const cartItemIndex = cartItems.findIndex(item => item.productId === productId);
    
    if (cartItemIndex >= 0) {
        const newQuantity = cartItems[cartItemIndex].quantity + change;
        
        if (newQuantity <= 0) {
            // Remove do carrinho
            cartItems.splice(cartItemIndex, 1);
            showTempMessage('Produto removido do carrinho!', 'success');
        } else if (newQuantity <= 10) {
            // Atualiza quantidade
            cartItems[cartItemIndex].quantity = newQuantity;
            cartItems[cartItemIndex].subtotal = newQuantity * cartItems[cartItemIndex].preco;
            showTempMessage('Quantidade atualizada!', 'info');
        } else {
            showTempMessage('Quantidade máxima é 10!', 'error');
            return; // Não atualiza se exceder o limite
        }
    }

    saveCartToStorage();
    renderProducts(allProducts); // ATUALIZA OS PRODUTOS
    updateCartDisplay(); // ATUALIZA O RESUMO DO CARRINHO
    window.location.reload();
    
    console.log('Carrinho atualizado:', cartItems);
}

// Carregar carrinho do localStorage
function loadCartFromStorage() {
    const savedCart = localStorage.getItem('modernStoreCart');
    if (savedCart) {
        try {
            const parsedCart = JSON.parse(savedCart);
            cartItems = parsedCart.map(item => {
                // Encontrar o produto completo na lista de produtos carregados
                const product = allProducts.find(p => p.id === item.productId);
                if (product) {
                    return {
                        productId: item.productId,
                        product: product,
                        quantity: item.quantity,
                        preco: item.preco,
                        subtotal: item.quantity * item.preco
                    };
                }
                return null;
            }).filter(item => item !== null); // Remover itens com produtos não encontrados
        } catch (error) {
            console.error('Erro ao carregar carrinho:', error);
            cartItems = [];
        }
    }
    updateCartDisplay();
}

// Salvar carrinho no localStorage
function saveCartToStorage() {
    const cartToSave = cartItems.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        preco: item.preco,
        subtotal: item.subtotal
    }));
    localStorage.setItem('modernStoreCart', JSON.stringify(cartToSave));
}

// Atualizar exibição do carrinho
function updateCartDisplay() {
    const cartItemsContainer = document.getElementById('cartItems');
    const summaryTotals = document.getElementById('summaryTotals');
    const emptyCart = document.getElementById('emptyCart');

    // Verificar se os elementos existem antes de manipular
    if (!cartItemsContainer || !summaryTotals || !emptyCart) {
        console.warn('Elementos do carrinho não encontrados no DOM. Tentando novamente...');
        
        // Tentar encontrar os elementos novamente após um delay
        setTimeout(() => {
            const retryCartItems = document.getElementById('cartItems');
            const retrySummaryTotals = document.getElementById('summaryTotals');
            const retryEmptyCart = document.getElementById('emptyCart');
            
            if (retryCartItems && retrySummaryTotals && retryEmptyCart) {
                console.log('Elementos encontrados na retentativa. Atualizando carrinho...');
                updateCartDisplay(); // Recursão para atualizar com elementos encontrados
            }
        }, 100);
        
        return;
    }

    console.log('Atualizando exibição do carrinho. Itens:', cartItems.length);

    if (cartItems.length === 0) {
        emptyCart.style.display = 'block';
        summaryTotals.style.display = 'none';
        console.log('Carrinho vazio exibido');
        return;
    }

    emptyCart.style.display = 'none';
    summaryTotals.style.display = 'block';

    // Renderizar itens do carrinho
    const itemsHTML = cartItems.map(item => {
        console.log('Renderizando item:', item);
        return `
            <div class="cart-item">
                <div class="item-image">
                    <img src="${item.product?.imgUrl || 'https://via.placeholder.com/100x100/e9ecef/6c757d?text=Produto'}" 
                         alt="${item.product?.nome || 'Produto'}"
                         onerror="this.src='https://via.placeholder.com/100x100/e9ecef/6c757d?text=Produto'">
                </div>
                <div class="item-details">
                    <div class="item-name">${item.product?.nome || 'Produto não encontrado'}</div>
                    <div class="item-category">${getProductCategories(item.product)}</div>
                    <div class="item-price">R$ ${(item.preco || 0).toFixed(2)}</div>
                </div>
                <div class="item-actions">
                    <div class="quantity-controls">
                        <button class="quantity-btn minus" onclick="updateProductQuantity(${item.productId}, -1)">-</button>
                        <span class="quantity">${item.quantity}</span>
                        <button class="quantity-btn plus" onclick="updateProductQuantity(${item.productId}, 1)">+</button>
                    </div>
                    <button class="remove-btn" onclick="removeFromCart(${item.productId})">
                        <i class="fas fa-trash"></i> Remover
                    </button>
                </div>
            </div>
        `;
    }).join('');

    cartItemsContainer.innerHTML = itemsHTML;

    // Calcular totais
    updateCartTotals();
    
    console.log('Carrinho atualizado com sucesso');
}

// Remover item do carrinho
function removeFromCart(productId) {
    console.log('Removendo produto:', productId);
    console.log('Carrinho antes:', cartItems);
    
    cartItems = cartItems.filter(item => item.productId !== productId);
    
    console.log('Carrinho depois:', cartItems);
    
    saveCartToStorage();
    renderProducts(allProducts);
    updateCartDisplay();
    
    // Feedback visual
    showTempMessage('Produto removido do carrinho!', 'success');
    window.location.reload();
}

// Atualizar totais do carrinho
function updateCartTotals() {
    const subtotalElement = document.getElementById('subtotal');
    const totalElement = document.getElementById('total');

    // Verificar se elementos existem
    if (!subtotalElement || !totalElement) {
        console.error('Elementos de totais não encontrados');
        return;
    }

    const subtotal = cartItems.reduce((total, item) => total + item.subtotal, 0);
    const total = subtotal;

    subtotalElement.textContent = `R$ ${subtotal.toFixed(2)}`;
    totalElement.textContent = `R$ ${total.toFixed(2)}`;
}

// Manipular busca
function handleSearch() {
    const searchTerm = document.getElementById('productSearch').value.trim();
    loadProducts(searchTerm);
}

// Finalizar compra (criar pedido)
async function handleCheckout() {
    if (cartItems.length === 0) {
        alert('Seu carrinho está vazio!');
        return;
    }

    try {
        // Preparar dados para criar o pedido
        const orderData = {
            items: cartItems.map(item => ({
                productId: item.productId,
                quantidade: item.quantity,
                preco: item.preco
                // Não enviar subtotal - será calculado no backend
            }))
        };

        console.log('Enviando dados do pedido:', orderData);

        const response = await authSystem.authenticatedFetch('/users/profile/orders', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orderData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Erro na resposta:', errorText);
            throw new Error(`Erro ao criar pedido: ${response.status} - ${errorText}`);
        }

        currentOrder = await response.json();
        console.log('Pedido criado com sucesso:', currentOrder);
        
        // Limpar carrinho após criar pedido
        cartItems = [];
        saveCartToStorage();
        updateCartDisplay();
        renderProducts(allProducts);
        
        // Mostrar modal de confirmação
        document.getElementById('confirmationModal').style.display = 'flex';

    } catch (error) {
        console.error('Erro detalhado ao finalizar compra:', error);
        alert('Erro ao finalizar compra: ' + error.message);
    }
}

// Continuar comprando
function continueShopping() {
    window.location.href = 'homepage.html';
}

// Utilitários
function getProductCategories(product) {
    if (product.categories && product.categories.length > 0) {
        return product.categories.map(cat => cat.nome).join(', ');
    }
    return 'Geral';
}

function getUserInitials(name) {
    return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Função para verificar e sincronizar autenticação (ATUALIZADA)
async function verifyAndSyncAuth() {
    console.log('=== SINCRONIZANDO AUTENTICAÇÃO ===');
    
    const hasToken = !!authSystem.getToken();
    let currentUser = authSystem.getCurrentUser(); // Tenta pegar do cache primeiro
    
    console.log('Token presente:', hasToken);
    console.log('Usuário no cache:', currentUser);
    
    if (hasToken && !currentUser) {
        console.log('Token presente mas usuário ausente no cache. Carregando do backend...');
        try {
            // Usar o loadCurrentUser para buscar dados atualizados
            currentUser = await authSystem.loadCurrentUser();
            console.log('Dados do usuário carregados do backend:', currentUser);
        } catch (error) {
            console.error('Erro ao carregar dados do usuário:', error);
            authSystem.logout();
            window.location.href = 'login.html';
            return false;
        }
    }
    
    if (!hasToken) {
        console.log('Token não encontrado. Redirecionando para login...');
        window.location.href = 'login.html';
        return false;
    }
    
    // Verificar se temos um usuário válido com ID
    if (!currentUser || !currentUser.id) {
        console.error('Usuário inválido ou sem ID:', currentUser);
        authSystem.logout();
        window.location.href = 'login.html';
        return false;
    }
    
    console.log('Autenticação válida. Usuário:', currentUser.nome);
    return true;
}

function showProductsLoading() {
    const loadingElement = document.getElementById('productsLoading');
    const gridElement = document.getElementById('productsGrid');
    
    if (loadingElement) loadingElement.style.display = 'block';
    if (gridElement) gridElement.style.display = 'none';
}

function hideProductsLoading() {
    const loadingElement = document.getElementById('productsLoading');
    const gridElement = document.getElementById('productsGrid');
    
    if (loadingElement) loadingElement.style.display = 'none';
    if (gridElement) gridElement.style.display = 'grid';
}

// Mostrar mensagem temporária
function showTempMessage(message, type = 'info') {
    const messageDiv = document.createElement('div');
    messageDiv.className = `temp-message ${type}`;
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 20px;
        background: ${type === 'success' ? '#28a745' : type === 'error' ? '#dc3545' : '#17a2b8'};
        color: white;
        border-radius: 4px;
        z-index: 1000;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(messageDiv);
    
    setTimeout(() => {
        messageDiv.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.parentNode.removeChild(messageDiv);
            }
        }, 300);
    }, 3000);
}

// Adicionar CSS para animações
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
`;
document.head.appendChild(style);

function showError(message) {
    console.error(message);
    alert(message);
}