const API_BASE = 'http://localhost:8080';
let userLoggedIn = false;
let currentUser = null;
let jwtToken = null;

// verifica se usuário está logado
function checkLogin() {
	const token = localStorage.getItem('jwtToken');
	if (token) {
		jwtToken = token;
		userLoggedIn = true;
		loadCurrentUser();
		return true;
	}
	return false;
}

// fazer login
async function login(login, password) {
	try {
		const response = await fetch(`${API_BASE}/auth/login`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify({
				login: login,        // Mudado de 'username' para 'login'
				senha: password     // Mantém 'senha' conforme backend
			})
		});

		if (!response.ok) {
			const errorData = await response.json();
			throw new Error(errorData.message || 'Credenciais inválidas');
		}

		const data = await response.json();
		localStorage.setItem('jwtToken', data.token);
		jwtToken = data.token;
		userLoggedIn = true;

		await loadCurrentUser();
		window.location.href = 'homepage.html';

	} catch (error) {
		console.error('Erro no login:', error);
		throw error;
	}
}

// fazer logout
function logout() {
	localStorage.removeItem('jwtToken');
	localStorage.removeItem('currentUser');
	userLoggedIn = false;
	currentUser = null;
	jwtToken = null;
	window.location.href = 'index.html';
}

// carregar dados do usuário atual
async function loadCurrentUser() {
	try {
		const response = await fetch(`${API_BASE}/users/profile`, {
			headers: {
				'Authorization': `Bearer ${jwtToken}`
			}
		});

		if (response.ok) {
			currentUser = await response.json();
			localStorage.setItem('currentUser', JSON.stringify(currentUser));
		} else {
			throw new Error('Falha ao carregar dados do usuário');
		}
	} catch (error) {
		console.error('Erro ao carregar usuário:', error);
		// Se não conseguir carregar o perfil, faz logout
		logout();
	}
}

// cadastrar novo usuário
async function register(userData) {
	try {
		// Mapeia os dados do frontend para o formato esperado pelo backend
		const registerData = {
			nome: userData.nome,           // Nome completo
			login: userData.login,         // Login único
			email: userData.email,         // E-mail
			telefone: userData.telefone,   // Telefone
			senha: userData.senha          // Senha
		};

		const response = await fetch(`${API_BASE}/auth/register`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(registerData)
		});

		if (!response.ok) {
			const errorData = await response.json();
			throw new Error(errorData.message || 'Erro no cadastro');
		}

		return await response.json();
	} catch (error) {
		console.error('Erro no cadastro:', error);
		throw error;
	}
}

// função para fazer requisições autenticadas
async function authenticatedFetch(url, options = {}) {
	if (!jwtToken) {
		checkLogin();
	}

	const defaultOptions = {
		headers: {
			'Authorization': `Bearer ${jwtToken}`,
			'Content-Type': 'application/json',
			...options.headers
		}
	};

	return fetch(`${API_BASE}${url}`, { ...defaultOptions, ...options });
}

// verificar login quando a página carrega
document.addEventListener('DOMContentLoaded', function() {
	userLoggedIn = checkLogin();

	// Proteger páginas que precisam de login
	if (window.location.pathname.includes('homepage.html') && !userLoggedIn) {
		window.location.href = 'index.html';
	}
	if (window.location.pathname.includes('pedidos') && !userLoggedIn) {
		window.location.href = 'login.html';
	}
	if (window.location.pathname.includes('perfil') && !userLoggedIn) {
		window.location.href = 'login.html';
	}
});

// exportar para uso em outros arquivos
window.authSystem = {
	login,
	logout,
	register,
	checkLogin,
	authenticatedFetch,
	getCurrentUser: () => currentUser,
	isLoggedIn: () => userLoggedIn,
	getToken: () => jwtToken
};