let allProducts = [];
let cartItems = [];
let currentOrder = null;

// Inicializar a página
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    initializePage();
    setupEventListeners();
});

// Verificar autenticação
function checkAuthentication() {
    if (!authSystem.isLoggedIn()) {
        alert('Você precisa estar logado para acessar o carrinho!');
        window.location.href = 'login.html';
        return;
    }
}

async function initializePage() {
    showProductsLoading();
    await updateNavigation();
    await loadProducts();
    loadCartFromStorage();
    hideProductsLoading();
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
            <span class="user-name">${userName}</span>
            <a href="homepage.html" class="btn btn-outline">
                <i class="fas fa-arrow-left"></i> Continuar Comprando
            </a>
            <div class="user-menu">
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

    searchInput.addEventListener('input', debounce(handleSearch, 300));
    searchBtn.addEventListener('click', handleSearch);
    checkoutBtn.addEventListener('click', handleCheckout);

    // Eventos do modal
    document.getElementById('continueShoppingBtn').addEventListener('click', function() {
        document.getElementById('confirmationModal').style.display = 'none';
        continueShopping();
    });

    document.getElementById('goToPaymentBtn').addEventListener('click', function() {
        if (currentOrder) {
            window.location.href = `pagamento.html?orderId=${currentOrder.id}`;
        }
    });
}

// Carregar produtos do backend
async function loadProducts(searchTerm = '') {
    try {
        let url = `${API_BASE}/products`;
        if (searchTerm) {
            url += `?q=${encodeURIComponent(searchTerm)}`;
        }

        const response = await fetch(url);
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
    
    if (products.length === 0) {
        productsGrid.innerHTML = '<div class="no-products">Nenhum produto encontrado.</div>';
        return;
    }

    const productsHTML = products.map(product => {
        const cartItem = cartItems.find(item => item.productId === product.id);
        const quantity = cartItem ? cartItem.quantity : 0;

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
                        <div class="quantity-controls">
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
}

// Adicionar produto ao carrinho
function addToCart(productId) {
    const product = allProducts.find(p => p.id === productId);
    if (!product) return;

    cartItems.push({
        productId: product.id,
        product: product,
        quantity: 1,
        preco: product.preco,
        subtotal: product.preco
    });

    saveCartToStorage();
    renderProducts(allProducts);
    updateCartDisplay();
}

// Atualizar quantidade do produto
function updateProductQuantity(productId, change) {
    const cartItemIndex = cartItems.findIndex(item => item.productId === productId);
    
    if (cartItemIndex >= 0) {
        const newQuantity = cartItems[cartItemIndex].quantity + change;
        
        if (newQuantity <= 0) {
            // Remove do carrinho
            cartItems.splice(cartItemIndex, 1);
        } else if (newQuantity <= 10) {
            // Atualiza quantidade
            cartItems[cartItemIndex].quantity = newQuantity;
            cartItems[cartItemIndex].subtotal = newQuantity * cartItems[cartItemIndex].preco;
        }
    }

    saveCartToStorage();
    renderProducts(allProducts);
    updateCartDisplay();
}

// Carregar carrinho do localStorage
function loadCartFromStorage() {
    const savedCart = localStorage.getItem('modernStoreCart');
    if (savedCart) {
        try {
            const parsedCart = JSON.parse(savedCart);
            // Recuperar informações completas dos produtos
            cartItems = parsedCart.map(item => {
                const product = allProducts.find(p => p.id === item.productId) || item.product;
                return {
                    ...item,
                    product: product,
                    subtotal: item.quantity * item.preco
                };
            });
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

    if (cartItems.length === 0) {
        emptyCart.style.display = 'block';
        summaryTotals.style.display = 'none';
        return;
    }

    emptyCart.style.display = 'none';
    summaryTotals.style.display = 'block';

    // Renderizar itens do carrinho
    const itemsHTML = cartItems.map(item => `
        <div class="cart-item">
            <div class="item-image">
                <img src="${item.product.imgUrl || 'https://via.placeholder.com/100x100/e9ecef/6c757d?text=Produto'}" 
                     alt="${item.product.nome}"
                     onerror="this.src='https://via.placeholder.com/100x100/e9ecef/6c757d?text=Produto'">
            </div>
            <div class="item-details">
                <div class="item-name">${item.product.nome}</div>
                <div class="item-category">${getProductCategories(item.product)}</div>
                <div class="item-price">R$ ${item.preco.toFixed(2)}</div>
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
    `).join('');

    cartItemsContainer.innerHTML = itemsHTML;

    // Calcular totais
    updateCartTotals();
}

// Remover item do carrinho
function removeFromCart(productId) {
    cartItems = cartItems.filter(item => item.productId !== productId);
    saveCartToStorage();
    renderProducts(allProducts);
    updateCartDisplay();
}

// Atualizar totais do carrinho
function updateCartTotals() {
    const subtotal = cartItems.reduce((total, item) => total + item.subtotal, 0);
    const total = subtotal;

    document.getElementById('subtotal').textContent = `R$ ${subtotal.toFixed(2)}`;
    document.getElementById('total').textContent = `R$ ${total.toFixed(2)}`;
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
        const orderData = {
            items: cartItems.map(item => ({
                productId: item.productId,
                quantidade: item.quantity,
                preco: item.preco,
                subtotal: item.subtotal
            }))
        };

        const response = await authSystem.authenticatedFetch('/users/profile/orders', {
            method: 'POST',
            body: JSON.stringify(orderData)
        });

        if (!response.ok) {
            throw new Error('Erro ao criar pedido');
        }

        currentOrder = await response.json();
        
        // Limpar carrinho após criar pedido
        cartItems = [];
        saveCartToStorage();
        updateCartDisplay();
        renderProducts(allProducts);
        
        // Mostrar modal de confirmação
        document.getElementById('confirmationModal').style.display = 'flex';

    } catch (error) {
        console.error('Erro ao finalizar compra:', error);
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

function showProductsLoading() {
    document.getElementById('productsLoading').style.display = 'block';
    document.getElementById('productsGrid').style.display = 'none';
}

function hideProductsLoading() {
    document.getElementById('productsLoading').style.display = 'none';
    document.getElementById('productsGrid').style.display = 'grid';
}

function showError(message) {
    console.error(message);
    alert(message);
}