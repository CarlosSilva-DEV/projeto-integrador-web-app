let currentProduct = null;
let relatedProducts = [];

// Inicializar a página
document.addEventListener('DOMContentLoaded', function() {
	initializeProductPage();
});

async function initializeProductPage() {
	await updateNavigation();
	loadCartFromStorage();
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
		navButtons.innerHTML = `
            <div class="nav-buttons-guest">
                <a href="login.html" class="btn btn-outline">Login</a>
                <a href="cadastro.html" class="btn btn-outline">Cadastro</a>
            </div>
        `;
	}
}

function goToAuthHomepage() {
	if (authSystem.isLoggedIn()) {
		window.location.href = 'homepage.html';
	} else {
		window.location.href = 'index.html';
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
}

// Atualizar breadcrumb
function updateBreadcrumb(product) {
	const breadcrumb = document.getElementById('breadcrumb');
	const categories = product.categories || [];
	const mainCategory = categories.length > 0 ? categories[0] : null;

	let breadcrumbHTML = '<a href="#" onclick="goToAuthHomepage()">Home</a> > ';

	if (mainCategory) {
		breadcrumbHTML += `<a href="#?category=${mainCategory.id}">${mainCategory.nome}</a> > `;
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
            <div class="product-card-image">
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
	try {
		if (!authSystem.isLoggedIn()) {
			document.getElementById('loginRedirectModal').style.display = 'flex';
			return;
		}

		loadCartFromStorage();

		const product = relatedProducts.find(p => p.id === productId);

		if (!product) {
			console.error('Produto não encontrado:', productId);
			showTempMessage('Produto não encontrado', 'error');
			return;
		}

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
        
        console.log('Carrinho após adição:', cartItems);
        
    } catch (error) {
        console.error('Erro ao adicionar produto:', error);
        showTempMessage('Erro ao adicionar produto', 'error');
    }
}

// Adicionar ao carrinho
function addToCart(productId, selectedQuantity) {
	try {
		if (!authSystem.isLoggedIn()) {
			document.getElementById('loginRedirectModal').style.display = 'flex';
			return;
		}

		loadCartFromStorage();

		const product = currentProduct;

		if (!product) {
			console.error('Produto não encontrado:', productId);
			showTempMessage('Produto não encontrado', 'error');
			return;
		}

		if (!selectedQuantity || selectedQuantity < 1) {
			selectedQuantity = 1;
		}

		if (selectedQuantity > 10) {
			showTempMessage('Quantidade máxima permitida é 10', 'error');
			return;
		}

		// Verificar se o produto já está no carrinho
		const existingItemIndex = cartItems.findIndex(item => item.productId === productId);

		if (existingItemIndex >= 0) {
			// Se já existe, incrementar quantidade
			const newQuantity = cartItems[existingItemIndex].quantity + selectedQuantity;

			if (newQuantity <= 10) {
				cartItems[existingItemIndex].quantity = newQuantity;
				cartItems[existingItemIndex].preco = product.preco; // Atualizar preço se mudou
				cartItems[existingItemIndex].subtotal = newQuantity * product.preco;
				cartItems[existingItemIndex].product = product; // Atualizar dados do produto
				showTempMessage(`Quantidade aumentada para ${newQuantity}!`, 'info');
			} else {
				showTempMessage('Quantidade máxima permitida é 10', 'error');
				return;
			}
		} else {
			// Se não existe, adicionar novo item com a quantidade selecionada
			const newItem = {
				productId: product.id,
				product: product,
				quantity: selectedQuantity,
				preco: product.preco,
				subtotal: product.preco * selectedQuantity
			};
			cartItems.push(newItem);
			showTempMessage(`${product.nome} (${selectedQuantity} unidade${selectedQuantity > 1 ? 's' : ''}) adicionado ao carrinho!`, 'success');
		}

		// Salvar no localStorage
		saveCartToStorage();

		console.log('Carrinho após adição:', cartItems);

	} catch (error) {
		console.error('Erro ao adicionar produto:', error);
		showTempMessage('Erro ao adicionar produto', 'error');
	}
}

// Carregar carrinho do localStorage
function loadCartFromStorage() {
	const savedCart = localStorage.getItem('modernStoreCart');
	if (savedCart) {
		try {
			const parsedCart = JSON.parse(savedCart);
			cartItems = parsedCart.map(item => {
				// Encontrar o produto completo na lista de produtos carregados
				if (currentProduct && currentProduct.id === item.productId) {
					return {
						productId: item.productId,
						product: currentProduct,
						quantity: item.quantity,
						preco: item.preco,
						subtotal: item.subtotal
					};
				}
				return {
					productId: item.productId,
					product: null,
					quantity: item.quantity,
					preco: item.preco,
					subtotal: item.subtotal
				};
			});
		} catch (error) {
			console.error('Erro ao carregar carrinho:', error);
			cartItems = [];
		}
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