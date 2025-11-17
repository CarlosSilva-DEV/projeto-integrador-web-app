let currentProduct = null;
let relatedProducts = [];

// Inicializar a página
document.addEventListener('DOMContentLoaded', function() {
	initializeProductPage();
});

async function initializeProductPage() {
	await updateNavigation();
	await loadProductData();
	setupEventListeners();
}

// Configurar navegação
async function updateNavigation() {
	const navButtons = document.getElementById('navButtons');
	const isLoggedIn = authSystem.isLoggedIn();

	if (isLoggedIn) {
		const currentUser = authSystem.getCurrentUser();
		const userName = currentUser ? currentUser.nome : 'Usuário';
		const initials = getUserInitials(userName);

		navButtons.innerHTML = `
            <div class="user-menu">
                <div class="user-avatar">${initials}</div>
                <span>${userName}</span>
                <div class="user-dropdown">
                    <a href="perfil.html"><i class="fas fa-user"></i> Meu Perfil</a>
                    <a href="#" onclick="authSystem.logout()"><i class="fas fa-sign-out-alt"></i> Sair</a>
                </div>
            </div>
            <button class="btn-icon" onclick="goToAuthHomepage()">
                <i class="fas fa-shopping-cart"></i> Área do Cliente
            </button>
        `;
	} else {
		navButtons.innerHTML = `
            <div class="nav-buttons-guest">
                <a href="login.html" class="btn">Login</a>
                <a href="cadastro.html" class="btn">Cadastro</a>
            </div>
        `;
	}
}

function goToAuthHomepage() {
	if (authSystem.isLoggedIn()) {
		window.location.href = 'homepage.html';
	} else {
		window.location.href = 'login.html';
	}
}

// Carregar dados do produto
async function loadProductData() {
	const urlParams = new URLSearchParams(window.location.search);
	const productId = urlParams.get('id');

	if (!productId) {
		showError('ID do produto não especificado');
		return;
	}

	try {
		showLoadingState();
		const product = await fetchProduct(productId);
		currentProduct = product;

		await renderProductDetails(product);
		await loadRelatedProducts(product);
		hideLoadingState();

	} catch (error) {
		console.error('Erro ao carregar produto:', error);
		showError('Erro ao carregar produto: ' + error.message);
	}
}

// Buscar produto na API
async function fetchProduct(productId) {
	const response = await fetch(`${API_BASE}/products/${productId}`);

	if (!response.ok) {
		throw new Error(`Produto não encontrado: ${response.status}`);
	}

	return await response.json();
}

// Renderizar detalhes do produto
async function renderProductDetails(product) {
	// Atualizar título da página
	document.title = `${product.nome} - ModernStore`;

	// Breadcrumb
	updateBreadcrumb(product);

	// Imagens
	renderProductImages(product);

	// Informações básicas
	document.getElementById('productCategory').textContent = getProductCategories(product);
	document.getElementById('productTitle').textContent = product.nome;
	document.getElementById('currentPrice').textContent = `R$ ${product.preco.toFixed(2)}`;
	document.getElementById('productDescription').textContent = product.descricao;

	// Especificações (se houver)
	renderProductSpecs(product);

	// Rating (placeholder - você pode adaptar conforme seu modelo)
	renderProductRating(product);
}

// Atualizar breadcrumb
function updateBreadcrumb(product) {
	const breadcrumb = document.getElementById('breadcrumb');
	const categories = product.categories || [];
	const mainCategory = categories.length > 0 ? categories[0] : null;

	let breadcrumbHTML = '<a href="index.html">Home</a> > ';

	if (mainCategory) {
		breadcrumbHTML += `<a href="index.html?category=${mainCategory.id}">${mainCategory.nome}</a> > `;
	}

	breadcrumbHTML += `<span>${product.nome}</span>`;
	breadcrumb.innerHTML = breadcrumbHTML;
}

// Renderizar imagens do produto
function renderProductImages(product) {
	const mainImage = document.getElementById('mainImage');
	const thumbnailsContainer = document.getElementById('imageThumbnails');

	// Usar imagem do produto ou placeholder
	const imageUrl = product.imgUrl || 'https://via.placeholder.com/500x500/e9ecef/6c757d?text=Produto';

	mainImage.src = imageUrl;
	mainImage.alt = product.nome;

	// Thumbnails (simples - uma única imagem por enquanto)
	thumbnailsContainer.innerHTML = `
        <div class="thumbnail active" data-image="${imageUrl}">
            <img src="${imageUrl}" alt="${product.nome}">
        </div>
    `;

	setupImageGallery();
}

// Renderizar especificações
function renderProductSpecs(product) {
	const specsContainer = document.getElementById('productSpecs');
	const specsList = document.getElementById('specsList');

	// Criar especificações baseadas nos dados disponíveis
	const specs = [
		{ label: 'Nome', value: product.nome },
		{ label: 'Descrição', value: product.descricao },
		{ label: 'Preço', value: `R$ ${product.preco.toFixed(2)}` }
	];

	// Adicionar categorias se existirem
	if (product.categories && product.categories.length > 0) {
		const categoriesText = product.categories.map(cat => cat.nome).join(', ');
		specs.push({ label: 'Categorias', value: categoriesText });
	}

	const specsHTML = specs.map(spec => `
        <li>
            <span class="spec-label">${spec.label}:</span>
            <span>${spec.value}</span>
        </li>
    `).join('');

	specsList.innerHTML = specsHTML;
	specsContainer.style.display = 'block';
}

// Renderizar rating (placeholder)
function renderProductRating(product) {
	const starsContainer = document.getElementById('productStars');
	// Aqui você pode implementar lógica de rating quando tiver no backend
	starsContainer.innerHTML = `
        <i class="far fa-star"></i>
        <i class="far fa-star"></i>
        <i class="far fa-star"></i>
        <i class="far fa-star"></i>
        <i class="far fa-star"></i>
    `;
}

// Carregar produtos relacionados
async function loadRelatedProducts(currentProduct) {
	try {
		const categories = currentProduct.categories || [];

		if (categories.length === 0) {
			// Se não há categorias, carrega produtos aleatórios
			await loadRandomProducts();
			return;
		}

		// Buscar produtos da mesma categoria
		const categoryId = categories[0].id;
		const response = await fetch(`${API_BASE}/products/by-category/${categoryId}`);

		if (response.ok) {
			const products = await response.json();
			// Filtrar o produto atual
			relatedProducts = products.filter(p => p.id !== currentProduct.id).slice(0, 4);
			renderRelatedProducts(relatedProducts);
		} else {
			await loadRandomProducts();
		}
	} catch (error) {
		console.error('Erro ao carregar produtos relacionados:', error);
		await loadRandomProducts();
	}
}

// Carregar produtos aleatórios como fallback
async function loadRandomProducts() {
	try {
		const response = await fetch(`${API_BASE}/products`);
		if (response.ok) {
			const products = await response.json();
			// Remover o produto atual e pegar 4 aleatórios
			relatedProducts = products
				.filter(p => !currentProduct || p.id !== currentProduct.id)
				.slice(0, 4);
			renderRelatedProducts(relatedProducts);
		}
	} catch (error) {
		console.error('Erro ao carregar produtos aleatórios:', error);
	}
}

// Renderizar produtos relacionados
function renderRelatedProducts(products) {
	const grid = document.getElementById('relatedProductsGrid');

	if (products.length === 0) {
		grid.innerHTML = '<p>Nenhum produto relacionado encontrado.</p>';
		return;
	}

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
                    <button class="btn btn-primary btn-small" onclick="addToCartFromRelated(${product.id})">
                        Comprar
                    </button>
                </div>
            </div>
        </div>
    `).join('');

	grid.innerHTML = productsHTML;
}

// Configurar event listeners
function setupEventListeners() {
	setupQuantityControls();
	setupFavoriteButton();
	setupAddToCartButton();
	setupModal();
}

// Configurar galeria de imagens
function setupImageGallery() {
	const thumbnails = document.querySelectorAll('.thumbnail');
	const mainImage = document.getElementById('mainImage');

	thumbnails.forEach(thumbnail => {
		thumbnail.addEventListener('click', function() {
			thumbnails.forEach(t => t.classList.remove('active'));
			this.classList.add('active');
			const imageUrl = this.dataset.image;
			mainImage.src = imageUrl;
		});
	});
}

// Configurar controles de quantidade
function setupQuantityControls() {
	const decreaseBtn = document.getElementById('decreaseQty');
	const increaseBtn = document.getElementById('increaseQty');
	const quantityInput = document.getElementById('quantityInput');

	decreaseBtn.addEventListener('click', () => {
		let value = parseInt(quantityInput.value);
		if (value > 1) quantityInput.value = value - 1;
	});

	increaseBtn.addEventListener('click', () => {
		let value = parseInt(quantityInput.value);
		if (value < parseInt(quantityInput.max)) quantityInput.value = value + 1;
	});

	quantityInput.addEventListener('change', function() {
		let value = parseInt(this.value);
		if (value < 1) this.value = 1;
		if (value > parseInt(this.max)) this.value = this.max;
	});
}

// Configurar botão de favorito
function setupFavoriteButton() {
	const favoriteBtn = document.getElementById('favoriteBtn');
	const icon = favoriteBtn.querySelector('i');

	favoriteBtn.addEventListener('click', function() {
		if (!authSystem.isLoggedIn()) {
			document.getElementById('loginRedirectModal').style.display = 'flex';
			return;
		}

		// Alternar estado do favorito
		if (icon.classList.contains('far')) {
			icon.classList.remove('far');
			icon.classList.add('fas');
			icon.style.color = 'var(--accent)';
			// Aqui você pode chamar uma API para adicionar aos favoritos
		} else {
			icon.classList.remove('fas');
			icon.classList.add('far');
			icon.style.color = '';
			// Aqui você pode chamar uma API para remover dos favoritos
		}
	});
}

// Configurar botão de adicionar ao carrinho
function setupAddToCartButton() {
	const addToCartBtn = document.getElementById('addToCartBtn');

	addToCartBtn.addEventListener('click', function() {
		if (!authSystem.isLoggedIn()) {
			document.getElementById('loginRedirectModal').style.display = 'flex';
			return;
		}

		if (currentProduct) {
			const quantity = parseInt(document.getElementById('quantityInput').value);
			addToCart(currentProduct.id, quantity);
		}
	});
}

// Configurar modal
function setupModal() {
	const cancelBtn = document.getElementById('cancelRedirectBtn');
	const modal = document.getElementById('loginRedirectModal');

	cancelBtn.addEventListener('click', () => modal.style.display = 'none');
	window.addEventListener('click', (e) => {
		if (e.target === modal) modal.style.display = 'none';
	});
}

// Funções utilitárias
function getProductCategories(product) {
	if (product.categories && product.categories.length > 0) {
		return product.categories.map(cat => cat.nome).join(' • ');
	}
	return 'Geral';
}

function getUserInitials(name) {
	return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
}

function showLoadingState() {
	document.body.style.opacity = '0.7';
}

function hideLoadingState() {
	document.body.style.opacity = '1';
}

function showError(message) {
	const productInfo = document.querySelector('.product-info');
	productInfo.innerHTML = `
        <div class="error-message">
            <i class="fas fa-exclamation-triangle"></i>
            <h3>Erro ao carregar produto</h3>
            <p>${message}</p>
            <a href="index.html" class="btn btn-primary">Voltar para Home</a>
        </div>
    `;
}

// Navegar para página de produto
function viewProduct(productId) {
	window.location.href = `produtos.html?id=${productId}`;
}

// Adicionar ao carrinho a partir de produtos relacionados
function addToCartFromRelated(productId) {
	if (!authSystem.isLoggedIn()) {
		document.getElementById('loginRedirectModal').style.display = 'flex';
		return;
	}

	const product = relatedProducts.find(p => p.id === productId);
	if (product) {
		addToCart(productId, 1);
	}
}

// Adicionar ao carrinho
function addToCart(productId, quantity) {
	if (!authSystem.isLoggedIn()) {
		document.getElementById('loginRedirectModal').style.display = 'flex';
		return;
	}

	// Aqui você implementará a lógica real do carrinho
	console.log(`Adicionando produto ${productId} ao carrinho, quantidade: ${quantity}`);
	alert('Produto adicionado ao carrinho!');

	// Em uma implementação real, você faria:
	// authSystem.authenticatedFetch('/cart/items', {
	//     method: 'POST',
	//     body: JSON.stringify({ productId, quantity })
	// });
}