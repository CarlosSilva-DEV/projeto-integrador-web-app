let currentOrderId = null;
let paymentData = null;
let isLoading = false;
let elements = {};

// Inicializar a página
document.addEventListener('DOMContentLoaded', async function () {
    checkAuthentication();

    currentOrderId = getOrderIdFromUrl();
    if (!currentOrderId) {
        showError('Pedido não encontrado na URL.');
        return;
    }

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
    await updateNavigation();
    cacheDomElements();
    setupEventListeners();
    await loadOrderData();
    updatePaymentStatus();
}

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

function cacheDomElements() {
    elements = {
        // Elementos do resumo do pedido
        orderIdElement: document.querySelector('.order-id'),
        orderDateElement: document.querySelector('.order-date'),
        orderItemsContainer: document.querySelector('.order-items'),
        totalElement: document.querySelector('.total-line.final span:last-child'),

        // Elementos de pagamento
        paymentStatusElement: document.querySelector('.payment-status span'),
        qrCodePlaceholder: document.querySelector('.qr-code-placeholder'),
        pixCodeElement: document.getElementById('pixCode'),
        copyPixBtn: document.getElementById('copyPixBtn'),
        payNowBtn: document.getElementById('payNowBtn'),
    };
}

function setupEventListeners() {
    // Copiar código PIX
    const copyPixBtn = document.getElementById('copyPixBtn');
    if (copyPixBtn) {
        copyPixBtn.addEventListener('click', copyPixCode);
    }

    // Confirmar pagamento
    const payNowBtn = document.getElementById('payNowBtn');
    if (payNowBtn) {
        payNowBtn.addEventListener('click', processPayment);
    }
}

// Retorna pedido pelo ID na URL
function getOrderIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId');

    if (!orderId) {
        // Tentar obter do localStorage
        const savedOrderId = localStorage.getItem('lastOrderId');
        if (savedOrderId) {
            localStorage.removeItem('lastOrderId');
            return savedOrderId;
        }

        console.error('OrderId não encontrado');
        return null;
    }
    return orderId;
}

// Carregando dados do pedido
async function loadOrderData() {
    if (!currentOrderId) return;

    showLoading(true);

    try {
        // Chamar endpoint para criar pagamento
        const response = await window.authSystem.authenticatedFetch(
            `/users/profile/orders/${currentOrderId}/payment`,
            {
                method: 'POST'
            }
        );

        if (!response.ok) {
            throw new Error(`Erro ao carregar pedido: ${response.status}`);
        }

        paymentData = await response.json();

        // Atualizar interface com dados reais
        updateOrderSummary(paymentData);
        updatePaymentStatus(paymentData.status || 'PENDENTE');
        fetchPixCode(paymentData);
        generateQRCode(paymentData);

    } catch (error) {
        console.error('Erro ao carregar dados do pedido:', error);
        showError('Não foi possível carregar os dados do pedido. Tente novamente.');
    } finally {
        showLoading(false);
    }
}

function fetchPixCode(paymentData) {
    const pixCodeElement = document.getElementById('pixCode');
    const pixCode = paymentData.pixCopiaCola || null;

    if (pixCodeElement) {
        pixCodeElement.innerHTML = `
        <span>${pixCode}</span>
        `;
    }
}

function generateQRCode(paymentData) {
    document.getElementById('qrCodePlaceholder').innerHTML = "";
    const payload = paymentData.pixQrCode || null;

    let qrCode = new QRCode(document.getElementById("qrCodePlaceholder"), {
        width: 200,
        height: 200,
        colorDark: "#000000",
        colorLight: "#ffffff",
        correctLevel: QRCode.CorrectLevel.H
    });

    qrCode.makeCode(payload);
}

function updateOrderSummary(paymentData) {
    if (!paymentData || !paymentData.order) return;

    const order = paymentData.order;

    // ID do pedido
    const orderIdElement = document.querySelector('.order-id');
    if (orderIdElement) {
        orderIdElement.textContent = `Pedido #${order.id}`;
    }

    // Data do pedido
    const orderDateElement = document.querySelector('.order-date');
    if (orderDateElement && order.moment) {
        const date = new Date(order.moment);
        const formattedDate = date.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
        orderDateElement.textContent = `Realizado em ${formattedDate}`;
    }

    // Itens do pedido
    const orderItemsContainer = document.querySelector('.order-items');
    if (orderItemsContainer) {
        orderItemsContainer.innerHTML = '';

        if (order.items && order.items.length > 0) {
            order.items.forEach(item => {
                const productName = item.product.nome || (item.product ? item.product.nome : 'Produto');
                const quantity = item.quantidade || 1;
                const price = item.preco || 0;

                const itemElement = document.createElement('div');
                itemElement.className = 'order-item';
                itemElement.innerHTML = `
                <div class="item-info">
                    <div class="item-name">${productName}</div>
                    <div class="item-details">
                        <span>Qtd: ${quantity}</span>
                </div>
                <div class="item-price">${formatCurrency(price)}</div>
            `;
                orderItemsContainer.appendChild(itemElement);
            })
        } else {
            orderItemsContainer.innerHTML = '<div class="no-items">Nenhum item encontrado</div>';
        }

    } else {
        console.error('Container .order-items não encontrado no DOM');
    }

    // Totais
    updateTotalElement('.total-line.final span:last-child', order.total || 0);
}

function updateTotalElement(selector, value) {
    const element = document.querySelector(selector);
    if (element) {
        element.textContent = formatCurrency(value);
    }
}

function updatePaymentStatus(paymentData) {
    if (!elements.paymentStatusElement || !paymentData) return;

    const status = paymentData.status || 'PENDENTE';
    elements.paymentStatusElement.textContent = status;

    // Atualizar cor baseada no status - FAZER: TORNAR PÁGINA DINÂMICA CONFORME PAGAMENTO TENHA SIDO EFETUADO 
    const statusElement = elements.paymentStatusElement.parentElement;
    statusElement.className = 'payment-status';

    switch (status) {
        case 'PENDENTE':
            statusElement.style.background = '#fff3e0';
            statusElement.style.color = '#ef6c00';
            break;
        case 'PAGO':
            statusElement.style.background = '#e8f5e9';
            statusElement.style.color = '#2e7d32';
            break;
        case 'PROCESSANDO':
            statusElement.style.background = '#e3f2fd';
            statusElement.style.color = '#1565c0';
            break;
        case 'CANCELADO':
            statusElement.style.background = '#ffebee';
            statusElement.style.color = '#c62828';
            break;
    }
}

async function processPayment() {
    if (!currentOrderId) return;

    // Desabilitar botão para evitar cliques duplos
    elements.payNowBtn.disabled = true;
    elements.payNowBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processando...';

    try {
        // Pedir confirmação do usuário
        const userConfirmed = confirm('Você já realizou o pagamento PIX?');
        if (userConfirmed) {
            await confirmPaymentBackend();
        }
    } catch (error) {
        console.error('Erro ao processar pagamento:', error);
        showError('Erro ao processar pagamento. Tente novamente.');
    } finally {
        // Reabilitar botão
        elements.payNowBtn.disabled = false;
        elements.payNowBtn.innerHTML = '<i class="fas fa-lock"></i> Confirmar Pagamento';
    }
}

async function confirmPaymentBackend() {
    try {
        const response = await window.authSystem.authenticatedFetch(
            `/users/profile/orders/${currentOrderId}/payment/confirm`,
            {
                method: 'POST'
            }
        );

        if (!response.ok) {
            throw new Error(`Erro ao confirmar pagamento: ${response.status}`);
        }

        const updatedOrder = await response.json();

        // Atualizar dados locais
        if (paymentData) {
            paymentData.order = updatedOrder;
        }

        // Atualizar interface
        updatePaymentStatus();

        // Mostrar mensagem de sucesso
        showSuccess('Pagamento confirmado com sucesso!');

        // Redirecionar após alguns segundos
        setTimeout(() => {
            window.location.href = 'perfil.html';
        }, 3000);

    } catch (error) {
        console.error('Erro ao confirmar pagamento no backend:', error);
        throw error;
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

function showError(message) {
    console.error(message);
    alert(message);
}

function viewCart() {
    if (!authSystem.isLoggedIn()) {
        alert('Faça login para ver o carrinho!');
        window.location.href = 'login.html';
        return;
    }

    window.location.href = 'carrinho.html';
}

function copyPixCode() {
    if (!elements.pixCodeElement) return;

    const text = elements.pixCodeElement.textContent;

    navigator.clipboard.writeText(text).then(() => {
        // Feedback visual
        const originalHTML = elements.copyPixBtn.innerHTML;
        elements.copyPixBtn.innerHTML = '<i class="fas fa-check"></i>';
        elements.copyPixBtn.style.background = '#28a745';

        setTimeout(() => {
            elements.copyPixBtn.innerHTML = originalHTML;
            elements.copyPixBtn.style.background = '';
        }, 2000);
    }).catch(err => {
        console.error('Erro ao copiar:', err);
        showError('Erro ao copiar código PIX');
    });
}

function formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

function showLoading(loading) {
    isLoading = loading;

    if (loading) {
        document.body.style.opacity = '0.7';
        document.body.style.pointerEvents = 'none';

        // Mostrar spinner se necessário
        const spinner = document.getElementById('loadingSpinner');
        if (!spinner) {
            const spinnerDiv = document.createElement('div');
            spinnerDiv.id = 'loadingSpinner';
            spinnerDiv.innerHTML = '<i class="fas fa-spinner fa-spin fa-3x"></i>';
            spinnerDiv.style.cssText = `
                position: fixed;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                z-index: 1000;
                color: var(--primary);
            `;
            document.body.appendChild(spinnerDiv);
        }
    } else {
        document.body.style.opacity = '1';
        document.body.style.pointerEvents = 'auto';

        const spinner = document.getElementById('loadingSpinner');
        if (spinner) {
            spinner.remove();
        }
    }
}

function showSuccess(message) {
    const toast = document.createElement('div');
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #4caf50;
        color: white;
        padding: 15px 20px;
        border-radius: var(--border-radius);
        box-shadow: var(--shadow);
        z-index: 1000;
        animation: slideIn 0.3s ease;
    `;

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}