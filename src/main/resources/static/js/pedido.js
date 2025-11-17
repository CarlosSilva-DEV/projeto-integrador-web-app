// Dados dos produtos disponíveis
const availableProducts = [
	{
		id: 1,
		name: "Smartphone Galaxy Pro",
		category: "Eletrônicos",
		price: 1299.99,
		image: "📱",
		stock: 10
	},
	{
		id: 2,
		name: "Notebook Ultra Slim",
		category: "Eletrônicos",
		price: 2499.99,
		image: "💻",
		stock: 5
	},
	{
		id: 3,
		name: "Fones de Ouvido Sem Fio",
		category: "Eletrônicos",
		price: 299.99,
		image: "🎧",
		stock: 15
	},
	{
		id: 4,
		name: "Camiseta Básica Premium",
		category: "Roupas",
		price: 49.99,
		image: "👕",
		stock: 20
	},
	{
		id: 5,
		name: "Tênis Esportivo Runner",
		category: "Esportes",
		price: 199.99,
		image: "👟",
		stock: 8
	},
	{
		id: 6,
		name: "Livro: A Arte da Programação",
		category: "Livros",
		price: 79.99,
		image: "📚",
		stock: 12
	},
	{
		id: 7,
		name: "Luminária de Mesa LED",
		category: "Casa e Decoração",
		price: 89.99,
		image: "💡",
		stock: 7
	},
	{
		id: 8,
		name: "Kit Maquiagem Profissional",
		category: "Beleza",
		price: 159.99,
		image: "💄",
		stock: 6
	}
];

// Carrinho de compras
let cart = [];

// Função para renderizar produtos
function renderProducts(products) {
	const productsGrid = document.getElementById('productsGrid');
	productsGrid.innerHTML = '';

	products.forEach(product => {
		const productCard = document.createElement('div');
		productCard.className = 'product-card';
		productCard.dataset.id = product.id;

		// Verificar se o produto já está no carrinho
		const cartItem = cart.find(item => item.id === product.id);
		const quantity = cartItem ? cartItem.quantity : 0;

		productCard.innerHTML = `
                    <div class="product-image">${product.image}</div>
                    <div class="product-info">
                        <div class="product-category">${product.category}</div>
                        <h3 class="product-name">${product.name}</h3>
                        <div class="product-price">R$ ${product.price.toFixed(2)}</div>
                        <div class="product-actions">
                            <div class="quantity-controls">
                                <button class="quantity-btn minus" ${quantity === 0 ? 'disabled' : ''}>-</button>
                                <input type="number" class="quantity-input" value="${quantity}" min="0" max="${product.stock}" readonly>
                                <button class="quantity-btn plus" ${quantity >= product.stock ? 'disabled' : ''}>+</button>
                            </div>
                            <button class="add-to-order" ${quantity > 0 ? 'disabled' : ''}>
                                ${quantity > 0 ? 'Adicionado' : 'Adicionar'}
                            </button>
                        </div>
                    </div>
                `;

		productsGrid.appendChild(productCard);
	});

	// Adicionar eventos aos botões de quantidade
	addQuantityEvents();
}

// Função para adicionar eventos de quantidade
function addQuantityEvents() {
	document.querySelectorAll('.quantity-btn.minus').forEach(btn => {
		btn.addEventListener('click', function() {
			const productCard = this.closest('.product-card');
			const productId = parseInt(productCard.dataset.id);
			updateProductQuantity(productId, -1);
		});
	});

	document.querySelectorAll('.quantity-btn.plus').forEach(btn => {
		btn.addEventListener('click', function() {
			const productCard = this.closest('.product-card');
			const productId = parseInt(productCard.dataset.id);
			updateProductQuantity(productId, 1);
		});
	});
}

// Função para atualizar quantidade do produto
function updateProductQuantity(productId, change) {
	const product = availableProducts.find(p => p.id === productId);
	const cartItemIndex = cart.findIndex(item => item.id === productId);

	if (cartItemIndex >= 0) {
		// Produto já está no carrinho
		const newQuantity = cart[cartItemIndex].quantity + change;

		if (newQuantity <= 0) {
			// Remove do carrinho se quantidade for zero
			cart.splice(cartItemIndex, 1);
		} else if (newQuantity <= product.stock) {
			// Atualiza quantidade
			cart[cartItemIndex].quantity = newQuantity;
			cart[cartItemIndex].total = newQuantity * product.price;
		}
	} else if (change > 0) {
		// Adiciona novo produto ao carrinho
		cart.push({
			id: product.id,
			name: product.name,
			price: product.price,
			quantity: 1,
			total: product.price,
			image: product.image
		});
	}

	updateCartDisplay();
	renderProducts(availableProducts);
}

// Função para atualizar exibição do carrinho
function updateCartDisplay() {
	const orderItems = document.getElementById('orderItems');
	const orderTotals = document.getElementById('orderTotals');
	const finishOrderBtn = document.getElementById('finishOrderBtn');

	if (cart.length === 0) {
		orderItems.innerHTML = `
                    <div class="empty-cart">
                        <i class="fas fa-shopping-cart"></i>
                        <p>Seu pedido está vazio</p>
                        <p>Adicione produtos para continuar</p>
                    </div>
                `;
		orderTotals.style.display = 'none';
		finishOrderBtn.disabled = true;
		return;
	}

	// Mostrar itens do carrinho
	let itemsHTML = '';
	let subtotal = 0;

	cart.forEach(item => {
		itemsHTML += `
                    <div class="order-item">
                        <div class="item-info">
                            <div class="item-name">${item.name}</div>
                            <div class="item-details">
                                <span>R$ ${item.price.toFixed(2)}</span>
                            </div>
                        </div>
                        <div class="item-quantity-controls">
                            <button class="quantity-btn minus" data-id="${item.id}">-</button>
                            <span class="item-quantity">${item.quantity}</span>
                            <button class="quantity-btn plus" data-id="${item.id}">+</button>
                            <button class="remove-item" data-id="${item.id}">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                        <div class="item-price">R$ ${item.total.toFixed(2)}</div>
                    </div>
                `;
		subtotal += item.total;
	});

	orderItems.innerHTML = itemsHTML;

	// Calcular totais
	const shipping = subtotal > 200 ? 0 : 15.00;
	const discount = subtotal > 500 ? subtotal * 0.1 : 0;
	const total = subtotal + shipping - discount;

	document.getElementById('subtotal').textContent = `R$ ${subtotal.toFixed(2)}`;
	document.getElementById('shipping').textContent = `R$ ${shipping.toFixed(2)}`;
	document.getElementById('discount').textContent = `- R$ ${discount.toFixed(2)}`;
	document.getElementById('total').textContent = `R$ ${total.toFixed(2)}`;

	orderTotals.style.display = 'block';
	finishOrderBtn.disabled = false;

	// Adicionar eventos aos botões do carrinho
	addCartEvents();
}

// Função para adicionar eventos ao carrinho
function addCartEvents() {
	document.querySelectorAll('.quantity-btn.minus[data-id]').forEach(btn => {
		btn.addEventListener('click', function() {
			const productId = parseInt(this.dataset.id);
			updateProductQuantity(productId, -1);
		});
	});

	document.querySelectorAll('.quantity-btn.plus[data-id]').forEach(btn => {
		btn.addEventListener('click', function() {
			const productId = parseInt(this.dataset.id);
			updateProductQuantity(productId, 1);
		});
	});

	document.querySelectorAll('.remove-item').forEach(btn => {
		btn.addEventListener('click', function() {
			const productId = parseInt(this.dataset.id);
			const cartItemIndex = cart.findIndex(item => item.id === productId);
			if (cartItemIndex >= 0) {
				cart.splice(cartItemIndex, 1);
				updateCartDisplay();
				renderProducts(availableProducts);
			}
		});
	});
}

// Função para finalizar pedido
function finishOrder() {
	// Simular criação do pedido no backend
	const orderData = {
		items: cart,
		status: "AGUARDANDO_PAGAMENTO",
		createdAt: new Date().toISOString(),
		orderId: "ORD-" + Date.now()
	};

	// Mostrar modal de confirmação
	document.getElementById('confirmationModal').style.display = 'flex';
}

// Inicializar a página
document.addEventListener('DOMContentLoaded', function() {
	// Renderizar produtos
	renderProducts(availableProducts);

	// Evento de busca
	document.getElementById('productSearch').addEventListener('input', function() {
		const searchTerm = this.value.toLowerCase();
		const filteredProducts = availableProducts.filter(product =>
			product.name.toLowerCase().includes(searchTerm) ||
			product.category.toLowerCase().includes(searchTerm)
		);
		renderProducts(filteredProducts);
	});

	// Evento do botão finalizar pedido
	document.getElementById('finishOrderBtn').addEventListener('click', finishOrder);

	// Eventos do modal
	document.getElementById('continueShoppingBtn').addEventListener('click', function() {
		document.getElementById('confirmationModal').style.display = 'none';
		// Limpar carrinho
		cart = [];
		updateCartDisplay();
		renderProducts(availableProducts);
	});

	document.getElementById('goToOrdersBtn').addEventListener('click', function() {
		// Redirecionar para a página de pedidos
		window.location.href = 'perfil.html';
	});
});