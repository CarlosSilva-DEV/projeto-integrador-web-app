const API_BASE = 'http://localhost:8080';
let userLoggedIn = false;
let currentUser = null;
let jwtToken = null;

// verificar estado de login
function checkLogin() {
    const token = localStorage.getItem('jwtToken');
    const storedUser = localStorage.getItem('currentUser');
    
    if (token) {
        try {
            jwtToken = token;
            
            // Verificar se o token é válido (não expirado)
            const payload = JSON.parse(atob(token.split('.')[1]));
            const exp = payload.exp * 1000;
            
            if (Date.now() < exp) {
                console.log('Token válido encontrado');
                
                // Se não temos usuário no localStorage, carregar do backend
                if (!storedUser) {
                    console.log('Token válido mas usuário não encontrado. Carregando do backend...');
                    loadCurrentUser().catch(error => {
                        console.error('Falha ao carregar usuário:', error);
                    });
                } else {
                    currentUser = JSON.parse(storedUser);
                }
                
                return true;
            } else {
                console.log('Token expirado');
                logout();
            }
        } catch (error) {
            console.error('Erro ao verificar token:', error);
            logout();
        }
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


function getCurrentUser() {
    try {
        if (currentUser) {
            return currentUser;
        }
        
        const userData = localStorage.getItem('currentUser');
        if (userData) {
            currentUser = JSON.parse(userData);
            return currentUser;
        }
        return null;
    } catch (error) {
        console.error('Erro ao recuperar usuário do localStorage:', error);
        return null;
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

// função para fazer requisições autenticadas (ATUALIZADA)
async function authenticatedFetch(url, options = {}) {
    // Verificar se o token existe
    if (!jwtToken) {
        console.warn('Token JWT não encontrado, verificando login...');
        checkLogin();
        if (!jwtToken) {
            window.location.href = 'login.html';
            return Promise.reject(new Error('Usuário não autenticado'));
        }
    }

    const defaultOptions = {
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json',
            ...options.headers
        }
    };

    const fetchOptions = { 
        ...defaultOptions, 
        ...options,
        headers: { ...defaultOptions.headers, ...options.headers }
    };

    console.log(`[authenticatedFetch] Fazendo requisição para: ${url}`);

    try {
        const response = await fetch(`${API_BASE}${url}`, fetchOptions);
        
        // Se receber 401, tentar renovar os dados
        if (response.status === 401) {
            console.warn('Token pode estar expirado. Tentando recarregar usuário...');
            try {
                await loadCurrentUser(); // Tentar recarregar usuário
                // Se conseguir recarregar, tentar a requisição novamente
                console.log('Usuário recarregado. Repetindo requisição...');
                return await fetch(`${API_BASE}${url}`, fetchOptions);
            } catch (refreshError) {
                console.error('Falha ao recarregar usuário:', refreshError);
                logout();
                window.location.href = 'login.html';
                return Promise.reject(new Error('Sessão expirada'));
            }
        }
        
        return response;
    } catch (error) {
        console.error('Erro na requisição autenticada:', error);
        throw error;
    }
}

// No seu processo de login, após receber o token:
async function handleLogin(credentials) {
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(credentials)
        });

        if (response.ok) {
            const data = await response.json();
            jwtToken = data.token;
            localStorage.setItem('jwtToken', jwtToken);
            
            // AGORA usar loadCurrentUser para buscar dados completos
            await loadCurrentUser();
            
            console.log('Login realizado com sucesso:', getCurrentUser());
            window.location.href = 'homepage.html';
        } else {
            throw new Error('Credenciais inválidas');
        }
    } catch (error) {
        console.error('Erro no login:', error);
        throw error;
    }
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