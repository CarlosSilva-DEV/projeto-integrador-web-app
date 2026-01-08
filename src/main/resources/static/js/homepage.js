let allProducts = [];
let allCategories = [];
let filteredProducts = [];
let cartItems = [];
let maxProductPrice = 1000; // Valor padrão, será atualizado

// Inicializar a página
document.addEventListener('DOMContentLoaded', function () {
    checkAuthentication();
    initializePage();
    setupEventListeners();
});

// Verificar autenticação
function checkAuthentication() {
    if (!authSystem.isLoggedIn()) {
        alert('Você precisa estar logado para acessar esta página!');
        window.location.href = 'login.html';
        return;
    }
}

async function initializePage() {
    showLoading();
    await updateNavigation();
    await updateUserWelcome();
    await loadCategories();
    await loadProducts();
    setupPriceFilter();
    hideLoading();
}

// Atualizar navegação - MODIFICADO: Nome do usuário no span
async function updateNavigation() {
    const navButtons = document.getElementById('navButtons');
    const isLoggedIn = authSystem.isLoggedIn();

    if (isLoggedIn) {
        const currentUser = authSystem.getCurrentUser();
        const userName = currentUser ? currentUser.nome : 'Usuário';
        const initials = getUserInitials(userName);

        navButtons.innerHTML = `
            <button class="btn btn-outline" onclick="viewCart()">
                <i class="fas fa-shopping-cart"></i> Carrinho
            </button>
            <div class="user-menu">
                <span class="user-name">${userName}</span>
                <div class="user-avatar">${initials}</div>
                <div class="user-dropdown">
                    <a href="perfil.html"><i class="fas fa-user"></i> Meu Perfil</a>
                    <a href="#" onclick="authSystem.logout()"><i class="fas fa-sign-out-alt"></i> Sair</a>
                </div>
            </div>
        `;
    } else {
        // Se não estiver logado, redireciona
        window.location.href = 'login.html';
    }
}

// Atualizar saudação do usuário - MODIFICADO: Nome completo
async function updateUserWelcome() {
    const currentUser = authSystem.getCurrentUser();
    if (currentUser && currentUser.nome) {
        // Usa o nome completo do usuário
        document.getElementById('welcomeMessage').textContent = `Olá, ${currentUser.nome}!`;
    }
}

// Configurar event listeners
function setupEventListeners() {
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const priceSlider = document.getElementById('priceSlider');

    searchInput.addEventListener('input', debounce(handleSearch, 300));
    searchBtn.addEventListener('click', handleSearch);
    priceSlider.addEventListener('input', handlePriceFilter);
}

// Carregar categorias
async function loadCategories() {
    try {
        console.log('Carregando categorias...');
        const response = await fetch(`${API_BASE}/categories`);

        if (!response.ok) {
            throw new Error(`Erro HTTP! status: ${response.status}`);
        }

        allCategories = await response.json();
        console.log('Categorias recebidas:', allCategories);
        renderCategories();
    } catch (error) {
        console.error('Erro ao carregar categorias:', error);
        showError('Erro ao carregar categorias: ' + error.message);
    }
}

// Carregar produtos
async function loadProducts(categoryId = null, searchTerm = '') {
    try {
        let url = `${API_BASE}/products`;
        const params = new URLSearchParams();

        if (categoryId) {
            params.append('category', categoryId);
        }

        if (searchTerm) {
            params.append('q', searchTerm);
        }

        if (params.toString()) {
            url += `?${params.toString()}`;
        }

        console.log('Fazendo request para:', url);
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`Erro HTTP! status: ${response.status}`);
        }

        allProducts = await response.json();
        console.log('Produtos recebidos:', allProducts);

        // Calcular preço máximo para o filtro
        updateMaxPrice(allProducts);

        filteredProducts = [...allProducts];
        renderProducts(filteredProducts);
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        showError('Erro ao carregar produtos: ' + error.message);
        document.getElementById('noProducts').style.display = 'block';
        document.getElementById('noProducts').innerHTML = `
            <p>Erro ao carregar produtos. Tente novamente mais tarde.</p>
            <p>Detalhes: ${error.message}</p>
        `;
    }
}

// Atualizar preço máximo baseado nos produtos
function updateMaxPrice(products) {
    if (products.length === 0) {
        maxProductPrice = 1000;
        return;
    }

    const prices = products.map(product => product.preco);
    maxProductPrice = Math.max(...prices);
    console.log('Preço máximo encontrado:', maxProductPrice);

    // Arredonda para cima para o próximo múltiplo de 100
    maxProductPrice = Math.ceil(maxProductPrice / 100) * 100;
}

// Configurar filtro de preço - MODIFICADO: Slider
function setupPriceFilter() {
    const priceSlider = document.getElementById('priceSlider');
    const maxPriceLabel = document.getElementById('maxPriceLabel');
    const selectedPriceValue = document.getElementById('selectedPriceValue');

    // Configura o slider com o preço máximo
    priceSlider.max = maxProductPrice;
    priceSlider.value = maxProductPrice;

    // Atualiza os labels
    maxPriceLabel.textContent = `R$ ${maxProductPrice.toFixed(2)}`;
    selectedPriceValue.textContent = `R$ ${maxProductPrice.toFixed(2)}`;
}

// Renderizar categorias
function renderCategories() {
    const categoriesList = document.getElementById('categoriesList');

    if (allCategories.length === 0) {
        categoriesList.innerHTML = '<li>Nenhuma categoria encontrada</li>';
        return;
    }

    const categoriesHTML = allCategories.map(category => `
        <li>
            <input type="checkbox" id="cat-${category.id}" value="${category.id}">
            <label for="cat-${category.id}">${category.nome}</label>
        </li>
    `).join('');

    categoriesList.innerHTML = categoriesHTML;

    // Adicionar event listeners
    allCategories.forEach(category => {
        const checkbox = document.getElementById(`cat-${category.id}`);
        checkbox.addEventListener('change', handleCategoryFilter);
    });
}

// Renderizar produtos
function renderProducts(products) {
    const productsGrid = document.getElementById('productsGrid');
    const noProducts = document.getElementById('noProducts');

    if (products.length === 0) {
        productsGrid.innerHTML = '';
        noProducts.style.display = 'block';
        return;
    }

    noProducts.style.display = 'none';

    const productsHTML = products.map(product => `
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
                    <button class="btn btn-outline btn-small" onclick="viewProduct(${product.id})">
                        Detalhes
                    </button>
                    <button class="btn btn-primary btn-small" onclick="addToCart(${product.id})">
                        Comprar
                    </button>
                </div>
            </div>
        </div>
    `).join('');

    productsGrid.innerHTML = productsHTML;
}

// Obter categorias do produto
function getProductCategories(product) {
    if (product.categories && product.categories.length > 0) {
        return product.categories.map(cat => cat.nome).join(', ');
    }
    return 'Geral';
}

// Manipular filtro de categoria
function handleCategoryFilter() {
    const selectedCategories = Array.from(document.querySelectorAll('.filter-category input:checked'))
        .map(checkbox => checkbox.value);

    if (selectedCategories.length === 0) {
        filteredProducts = [...allProducts];
    } else {
        filteredProducts = allProducts.filter(product =>
            product.categories && product.categories.some(cat =>
                selectedCategories.includes(cat.id.toString())
            )
        );
    }

    renderProducts(filteredProducts);
}

// Manipular filtro de preço - MODIFICADO: Slider
function handlePriceFilter() {
    const priceSlider = document.getElementById('priceSlider');
    const selectedPriceValue = document.getElementById('selectedPriceValue');

    const selectedPrice = parseFloat(priceSlider.value);

    // Atualiza o valor exibido
    selectedPriceValue.textContent = `R$ ${selectedPrice.toFixed(2)}`;

    // Filtra os produtos
    filteredProducts = allProducts.filter(product =>
        product.preco <= selectedPrice
    );

    renderProducts(filteredProducts);
}

// Manipular busca
function handleSearch() {
    const searchTerm = document.getElementById('searchInput').value.trim();

    if (searchTerm) {
        loadProducts(null, searchTerm);
    } else {
        loadProducts();
    }
}

// Ver produto detalhado
function viewProduct(productId) {
    window.location.href = `produtos.html?id=${productId}`;
}

// Adicionar ao carrinho
function addToCart(productId) {
    try {
        if (!authSystem.isLoggedIn()) {
            alert('Faça login para adicionar produtos ao carrinho!');
            window.location.href = 'login.html';
            return;
        }

        const product = allProducts.find(p => p.id === productId);
        if (product) {
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
                alert(`${product.nome} adicionado ao carrinho!`);
                console.log('Novo item adicionado:', newItem);
            }

            saveCartToStorage();

            // Atualizar a exibição dos produtos (para mostrar controles de quantidade)
            renderProducts(allProducts);

            console.log('Carrinho após adição:', cartItems);
        }
    } catch (error) {
        console.error('Erro ao adicionar produto:', error);
        console.log('Erro ao adicionar produto', 'error');
    }
}

// salvar item no carrinho
function saveCartToStorage() {
    const cartToSave = cartItems.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        preco: item.preco,
        subtotal: item.subtotal
    }));
    localStorage.setItem('modernStoreCart', JSON.stringify(cartToSave));
}

// Ver carrinho
function viewCart() {
    if (!authSystem.isLoggedIn()) {
        alert('Faça login para ver o carrinho!');
        window.location.href = 'login.html';
        return;
    }

    window.location.href = 'carrinho.html';
}

// Utilitários
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

function showLoading() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('productsGrid').style.display = 'none';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
    document.getElementById('productsGrid').style.display = 'grid';
}

function showError(message) {
    console.error(message);
    // Você pode adicionar um toast ou alerta visual aqui
    alert(message);
}