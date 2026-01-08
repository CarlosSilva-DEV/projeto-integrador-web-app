let allProducts = [];
let allCategories = [];
let filteredProducts = [];
let maxProductPrice = 1000;

document.addEventListener('DOMContentLoaded', function () {
    initializePage();
    setupEventListeners();
});

async function initializePage() {
    showLoading();
    await loadCategories();
    await loadProducts();
    setupPriceFilter();
    hideLoading();
}

function setupEventListeners() {
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const priceSlider = document.getElementById('priceSlider')

    searchInput.addEventListener('input', debounce(handleSearch, 300));
    searchBtn.addEventListener('click', handleSearch);
    priceSlider.addEventListener('input', handlePriceFilter);
}


function goToAuthHomepage() {
    if (authSystem.isLoggedIn()) {
        window.location.href = 'homepage.html';
    } else {
        window.location.href = 'login.html';
    }
}

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
        // Fazer busca na API
        loadProducts(null, searchTerm);
    } else {
        // Voltar para todos os produtos
        loadProducts();
    }
}

// Ver produto detalhado
function viewProduct(productId) {
    window.location.href = `produtos.html?id=${productId}`;
}

// Adicionar ao carrinho (versão simplificada para usuários não logados)
function addToCart(productId) {
    if (!authSystem.isLoggedIn()) {
        alert('Faça login para adicionar produtos ao carrinho!');
        window.location.href = 'login.html';
        return;
    }

    const product = allProducts.find(p => p.id === productId);
    if (product) {
        alert(`${product.nome} adicionado ao carrinho!`);
        // Aqui você pode integrar com o sistema de carrinho real
    }
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
}