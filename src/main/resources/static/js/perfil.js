let userProfile = null;
let userOrders = [];

// Inicializar a página
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    initializePage();
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
    await loadUserProfile();
    await loadUserOrders();
    setupEventListeners();
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
            <a href="carrinho.html" class="btn btn-outline">
                <i class="fas fa-shopping-cart"></i> Carrinho
            </a>
            <div class="user-menu">
                <div class="user-avatar">${initials}</div>
                <div class="user-dropdown">
                    <a href="perfil.html" class="active"><i class="fas fa-user"></i> Meu Perfil</a>
                    <a href="#" onclick="authSystem.logout()"><i class="fas fa-sign-out-alt"></i> Sair</a>
                </div>
            </div>
        `;
    }
}

// Verificar elementos essenciais do DOM
function ensureEssentialElements() {
    const essentialElements = [
        'navButtons',
        'profileName',      
        'profileAvatar',    
        'profileStatus',    
        'userFullName',     
        'userLogin',        
        'userEmail',        
        'userPhone',        
        'userPassword',     
        'userRole',         
        'ordersList',       
        'ordersLoading'     
    ];
	
    const optionalElements = ['userName', 'userAvatar'];

    const missingEssential = essentialElements.filter(id => !document.getElementById(id));
    const missingOptional = optionalElements.filter(id => !document.getElementById(id));
    
    if (missingEssential.length > 0) {
        console.error('Elementos essenciais não encontrados:', missingEssential);
        return false;
    }

    if (missingOptional.length > 0) {
        console.warn('Elementos opcionais não encontrados:', missingOptional);
    }
    
    return true;
}

// Carregar perfil do usuário do backend
async function loadUserProfile() {
    try {
        const response = await authSystem.authenticatedFetch('/users/profile');
        
        if (!response.ok) {
            throw new Error('Erro ao carregar perfil do usuário');
        }

        userProfile = await response.json();
        updateProfileDisplay();
        
    } catch (error) {
        console.error('Erro ao carregar perfil:', error);
        showError('Erro ao carregar perfil: ' + error.message);
    }
}

// Carregar pedidos do usuário do backend
async function loadUserOrders() {
    try {
        showOrdersLoading();
        
        const response = await authSystem.authenticatedFetch('/users/profile/orders');
        
        if (!response.ok) {
            throw new Error('Erro ao carregar pedidos');
        }

        userOrders = await response.json();
        renderOrders();
        hideOrdersLoading();
        
    } catch (error) {
        console.error('Erro ao carregar pedidos:', error);
        hideOrdersLoading();
        document.getElementById('ordersList').innerHTML = `
            <div class="error-message">
                <i class="fas fa-exclamation-triangle"></i>
                <p>Erro ao carregar pedidos: ${error.message}</p>
            </div>
        `;
    }
}

// Atualizar exibição do perfil
function updateProfileDisplay() {
    if (!userProfile) {
        console.warn('Perfil do usuário não carregado');
        return;
    }

    console.log('Atualizando perfil com dados:', userProfile);

    // Função auxiliar para atualizar elementos com segurança
    function safeUpdateElement(id, value, property = 'textContent') {
        const element = document.getElementById(id);
        if (element) {
            element[property] = value;
            console.log(`Elemento ${id} atualizado:`, value);
        } else {
            console.warn(`Elemento não encontrado: ${id}`);
        }
    }

    // Função auxiliar para atualizar classe com segurança
    function safeUpdateClass(id, className, condition) {
        const element = document.getElementById(id);
        if (element) {
            element.className = condition ? className : `${className} empty`;
        }
    }

    // ATUALIZAÇÃO: Usar apenas IDs que existem no HTML
    
    // Header da página (se você adicionou como sugeri)
    safeUpdateElement('userName', userProfile.nome);
    
    const initials = getUserInitials(userProfile.nome);
    safeUpdateElement('userAvatar', initials);
    
    // Sidebar do perfil (EXISTE no HTML)
    safeUpdateElement('profileName', userProfile.nome);
    safeUpdateElement('profileAvatar', initials);
    
    // Define o status baseado na role
    const status = userProfile.role === 'ROLE_ADMIN' ? 'Administrador' : 'Cliente';
    safeUpdateElement('profileStatus', status);
    
    // Informações pessoais (EXISTEM no HTML)
    safeUpdateElement('userFullName', userProfile.nome || 'Não informado');
    safeUpdateClass('userFullName', 'info-value', userProfile.nome);
    
    safeUpdateElement('userLogin', userProfile.login || 'Não informado');
    safeUpdateClass('userLogin', 'info-value', userProfile.login);
    
    safeUpdateElement('userEmail', userProfile.email || 'Não informado');
    safeUpdateClass('userEmail', 'info-value', userProfile.email);
    
    safeUpdateElement('userPhone', userProfile.telefone || 'Não informado');
    safeUpdateClass('userPhone', 'info-value', userProfile.telefone);
    
    // Sempre mostra senha mascarada
    safeUpdateElement('userPassword', '••••••••');
    safeUpdateClass('userPassword', 'info-value', true);
    
    // Mostra a role traduzida
    const roleText = userProfile.role === 'ROLE_ADMIN' ? 'Administrador' : 'Usuário';
    safeUpdateElement('userRole', roleText);
    safeUpdateClass('userRole', 'info-value', true);

    console.log('Perfil atualizado com sucesso');
}

// Renderizar pedidos
function renderOrders() {
    const ordersList = document.getElementById('ordersList');
    
    if (userOrders.length === 0) {
        ordersList.innerHTML = `
            <div class="empty-orders">
                <i class="fas fa-shopping-bag"></i>
                <h3>Nenhum pedido realizado</h3>
                <p>Que tal fazer sua primeira compra?</p>
                <button class="btn btn-primary" onclick="startNewOrder()">
                    <i class="fas fa-plus"></i> Fazer Primeiro Pedido
                </button>
            </div>
        `;
        return;
    }

    const ordersHTML = userOrders.map(order => {
        const orderDate = new Date(order.moment).toLocaleDateString('pt-BR');
        const orderTime = new Date(order.moment).toLocaleTimeString('pt-BR');
        const status = getOrderStatusText(order.orderStatus);
        const statusClass = getOrderStatusClass(order.orderStatus);
        const total = order.total || calculateOrderTotal(order);

        return `
            <div class="order-card">
                <div class="order-header">
                    <div class="order-info">
                        <span class="order-id">Pedido #${order.id}</span>
                        <span class="order-date">${orderDate} às ${orderTime}</span>
                    </div>
                    <div class="order-status ${statusClass}">${status}</div>
                </div>
                <div class="order-items">
                    ${order.items ? order.items.map(item => `
                        <div class="order-item">
                            <div class="item-image">
                                <img src="${item.product?.imgUrl || 'https://via.placeholder.com/80x80/e9ecef/6c757d?text=Produto'}" 
                                     alt="${item.product?.nome || 'Produto'}"
                                     onerror="this.src='https://via.placeholder.com/80x80/e9ecef/6c757d?text=Produto'">
                            </div>
                            <div class="item-details">
                                <div class="item-name">${item.product?.nome || 'Produto não encontrado'}</div>
                                <div class="item-category">${getProductCategories(item.product)}</div>
                                <div class="item-price">R$ ${item.preco?.toFixed(2) || '0.00'}</div>
                            </div>
                            <div class="item-quantity">Qtd: ${item.quantidade}</div>
                        </div>
                    `).join('') : '<p>Nenhum item encontrado</p>'}
                </div>
                <div class="order-footer">
                    <div class="order-total">Total: R$ ${total.toFixed(2)}</div>
                    <div class="order-actions">
                        ${order.orderStatus === 'AGUARDANDO_PAGAMENTO' ? `
                            <button class="order-action primary" onclick="payOrder(${order.id})">
                                <i class="fas fa-credit-card"></i> Pagar
                            </button>
                            <button class="order-action" onclick="cancelOrder(${order.id})">
                                <i class="fas fa-times"></i> Cancelar
                            </button>
                        ` : ''}
                    </div>
                </div>
            </div>
        `;
    }).join('');

    ordersList.innerHTML = ordersHTML;
}

// Calcular total do pedido
function calculateOrderTotal(order) {
    if (!order.items) return 0;
    return order.items.reduce((total, item) => total + (item.subtotal || 0), 0);
}

// Obter texto do status do pedido
function getOrderStatusText(status) {
    const statusMap = {
        'AGUARDANDO_PAGAMENTO': 'Aguardando Pagamento',
        'PAGO': 'Pago',
        'ENVIADO': 'Enviado',
        'ENTREGUE': 'Entregue',
        'CANCELADO': 'Cancelado'
    };
    return statusMap[status] || status;
}

// Obter classe CSS do status do pedido
function getOrderStatusClass(status) {
    const classMap = {
        'AGUARDANDO_PAGAMENTO': 'status-pending',
        'PAGO': 'status-processing',
        'ENVIADO': 'status-processing',
        'ENTREGUE': 'status-delivered',
        'CANCELADO': 'status-cancelled'
    };
    return classMap[status] || 'status-pending';
}

// Obter categorias do produto
function getProductCategories(product) {
    if (!product) return 'Geral';
    if (product.categories && product.categories.length > 0) {
        return product.categories.map(cat => cat.nome).join(', ');
    }
    return 'Geral';
}

// Configurar event listeners
function setupEventListeners() {
    document.getElementById('profileForm').addEventListener('submit', handleProfileUpdate);
    
    // Fecha modal ao clicar fora
    document.getElementById('editModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeEditModal();
        }
    });
}

// Modal de edição
function openEditModal() {
    if (!userProfile) return;
    
    document.getElementById('editModal').style.display = 'flex';
    
    // Preenche o formulário com dados atuais
    document.getElementById('editFullName').value = userProfile.nome || '';
    document.getElementById('editEmail').value = userProfile.email || '';
    document.getElementById('editPhone').value = userProfile.telefone || '';
    document.getElementById('editPassword').value = '';
}

function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
    // Limpa o formulário
    document.getElementById('profileForm').reset();
}

// Atualizar perfil
async function handleProfileUpdate(event) {
    event.preventDefault();
    
    const updateData = {
        nome: document.getElementById('editFullName').value.trim(),
        email: document.getElementById('editEmail').value.trim(),
        telefone: document.getElementById('editPhone').value.trim()
    };
    
    const newPassword = document.getElementById('editPassword').value.trim();
    if (newPassword) {
        if (newPassword.length < 6) {
            alert('A senha deve ter no mínimo 6 caracteres');
            return;
        }
        updateData.senha = newPassword;
    }
    
    try {
        const response = await authSystem.authenticatedFetch('/users/profile', {
            method: 'PATCH',
            body: JSON.stringify(updateData)
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erro ao atualizar perfil');
        }
        
        const updatedUser = await response.json();
        userProfile = updatedUser;
        
        // Atualiza o usuário no auth system
        localStorage.setItem('currentUser', JSON.stringify(updatedUser));
        
        updateProfileDisplay();
        closeEditModal();
        
        alert('Perfil atualizado com sucesso!');
        
    } catch (error) {
        console.error('Erro ao atualizar perfil:', error);
        alert('Erro ao atualizar perfil: ' + error.message);
    }
}

// Funções dos pedidos
function startNewOrder() {
    window.location.href = 'carrinho.html';
}

function viewOrderDetails(orderId) {
    alert(`Visualizando detalhes do pedido #${orderId}`);
    // Em uma implementação real, você redirecionaria para uma página de detalhes do pedido
}

async function payOrder(orderId) {
    try {
        const response = await authSystem.authenticatedFetch(`/users/profile/orders/${orderId}/payment`, {
            method: 'POST'
        });
        
        if (!response.ok) {
            throw new Error('Erro ao processar pagamento');
        }
        
        alert('Pagamento processado com sucesso!');
        // Recarrega os pedidos para atualizar o status
        await loadUserOrders();
        
    } catch (error) {
        console.error('Erro ao processar pagamento:', error);
        alert('Erro ao processar pagamento: ' + error.message);
    }
}

async function cancelOrder(orderId) {
    if (!confirm('Tem certeza que deseja cancelar este pedido?')) {
        return;
    }
    
    try {
        const response = await authSystem.authenticatedFetch(`/users/profile/orders/${orderId}/cancel`, {
            method: 'POST'
        });
        
        if (!response.ok) {
            throw new Error('Erro ao cancelar pedido');
        }
        
        alert('Pedido cancelado com sucesso!');
        // Recarrega os pedidos para atualizar o status
        await loadUserOrders();
        
    } catch (error) {
        console.error('Erro ao cancelar pedido:', error);
        alert('Erro ao cancelar pedido: ' + error.message);
    }
}

// Utilitários
function getUserInitials(name) {
    if (!name) return 'U';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
}

function showOrdersLoading() {
    document.getElementById('ordersLoading').style.display = 'block';
    document.getElementById('ordersList').style.display = 'none';
}

function hideOrdersLoading() {
    document.getElementById('ordersLoading').style.display = 'none';
    document.getElementById('ordersList').style.display = 'block';
}

function showError(message) {
    console.error(message);
    // Você pode implementar um sistema de notificação mais sofisticado aqui
    alert(message);
}