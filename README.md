<h1 align="center" style="font-weight: bold">PROJETO INTEGRADOR - APLICAÇÃO WEB 💻</h1>

<p align="center">
  <a href="#visão-geral">Visão Geral</a> •
  <a href="#tecnologias-utilizadas">Tecnologias Utilizadas</a> •
  <a href="#técnicas-utilizadas">Técnicas Utilizadas</a> •
  <a href="#como-testar-o-projeto">Como testar o projeto?</a> •
  <a href="#documentação-da-api">Documentação da API</a> •
  <a href="#contribuir-para-o-projeto">Contribuir para o projeto</a>
</p>

<br>

<div align="center">
  <img src=https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white>
  <img src=https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=>
  <img src=https://img.shields.io/badge/Javascript-000?style=for-the-badge&logo=javascript>
  <img src=https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white>
  <img src=https://img.shields.io/badge/apachemaven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white>
  <img src=https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white>
  <img src=https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white>
  <br>
  <img src=https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens>
  <img src=https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white>
  <img src=https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white>
  <img src=https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white>
  <img src=https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white>
  <img src=https://img.shields.io/badge/Visual%20Studio%20Code-0078d7.svg?style=for-the-badge&logo=visual-studio-code&logoColor=white>
  <img src=https://img.shields.io/badge/Eclipse-FE7A16.svg?style=for-the-badge&logo=Eclipse&logoColor=white>
</div>

<br>

## VISÃO GERAL

Esse projeto consiste em uma aplicação completa com frontend baseado em web, a qual simula um e-commerce de aparelhos eletrônicos e acessórios. O projeto foi inicialmente desenvolvido como um trabalho acadêmico para o Projeto Integrador do 4º semestre do curso de Tecnologia em Análise e Desenvolvimento de Sistemas, inspirado por um mini projeto do [Curso de Java do Professor Nélio Alves](https://www.udemy.com/course/java-curso-completo/?srsltid=AfmBOor-J_Mz_UZURktDzuShECN7urEuXtyYb4VXI0mEOS1iaMKJBlvQ), com a adição de novas funcionalidades como uma **camada de autenticação de usuários**, **sistema de roles (papéis) de usuários** e **integração com frontend básico para interação client-side**. Essa aplicação foi desenvolvida para fins acadêmicos, com o intuito de pôr em prática os conhecimentos de desenvolvimento de software que venho adquirindo.

<br>

## TECNOLOGIAS UTILIZADAS

### FRONTEND
- HTML5 (Conteúdo semântico das páginas web)
- CSS3 (Estilização de páginas web)
- JavaScript (Integração com API server-side para requisições e dinamismo em páginas web)
- QRCode.js (Biblioteca client-side para geração de QR Codes)

### BACKEND
- Java (OpenJDK 17)
- Maven 4.0.0 (Gerenciador de Dependências)
- Spring Boot 3.5.7 (framework Java para aplicações web)
- Spring Data JPA (API de persistência de dados)
- Hibernate (Mapeamento objeto-relacional)
- Spring Security (Autenticação e segurança baseada em roles)
- JSON Web Token/JWT (Geração de tokens com credenciais de autenticação)
- Spring Validation (Validação de entrada de dados)
- H2 (Banco de dados para testes)
- PostgreSQL (Banco de dados para desenvolvimento e produção)
- Docker Compose (Build e gerenciamento de containers para ambiente de desenvolvimento)
- Postman (Testes de requisições HTTP em API backend)
- Swagger (Documentação de endpoints da API)

<br>

## TÉCNICAS UTILIZADAS

- Desenvolvimento no Visual Studio Code e Spring Tools Suite (Eclipse)
- Paradigmas de Programação Orientada a Objetos e Estruturas de Dados
- Arquitetura Monolítica
- Padrão de Projeto MVC (Model-View-Controller)
- Relacionamentos de entidades
- API RESTful
- Operações CRUD
- Tratamento de Exceções
- Delegação de papéis entre Usuários (roles)
- Sistema de autenticação stateless para validação de requisições dos Usuários
- Data Transfer Objects (DTOs)

<br>

## COMO TESTAR O PROJETO?

### Pré-requisitos

- Docker Engine
- Docker Compose

### 1. Clonar repositório
Em seu computador, abra o terminal e navegue até um diretório de sua preferência para armazenar o projeto. Execute o comando abaixo para clonar o repositório remoto para seu computador: 
```bash
git clone git@github.com:CarlosSilva-DEV/projeto-integrador-web-app.git
```

### 2. Build do projeto com docker-compose
Após clonar o projeto, execute o comando abaixo para realizar o build dos containers da aplicação e do banco de dados. Aguarde a conclusão e a aplicação será inicializada:
```bash
sudo docker-compose up --build
```

### Adicionais

- Caso você tenha uma instância do PostgreSQL instalada em seu computador, verifique se o serviço está em execução **antes de iniciar o build com docker-compose**, a fim de evitar conflitos entre portas:
```bash
# Verificar se o serviço PostgreSQL está em execução
sudo systemctl status postgresql

# Interromper a execução do serviço PostgreSQL
sudo systemctl stop postgresql
```

- Posteriormente, caso queira verificar ou excluir os containers criados, utilize os comandos abaixo:
```bash
# Verifica as instâncias de container criadas
sudo docker-compose ps

# Exclui as instâncias de container criadas (incluindo o volume do banco de dados)
sudo docker-compose down -v
```

<br>

## DOCUMENTAÇÃO DA API

A API backend de uma aplicação web serve para receber requisições de seus usuários, processá-las e retornar suas respectivas respostas. No contexto desse projeto, a API possui endpoints livres para acesso por qualquer **usuário autenticado** e endpoints restritos a **usuários ADMIN**. Caso deseje testar requisições restritas a usuários ADMIN, consulte a classe [AdminConfig.java](https://github.com/CarlosSilva-DEV/projeto-integrador-web-app/blob/main/src/main/java/com/carlossilvadev/projeto_integrador_web_app/config/AdminConfig.java), a qual contém os **dados do ADMIN padrão criado na inicialização da aplicação**.

Seguem abaixo algumas demonstrações de como realizar requisições nos principais endpoints da aplicação. Para mais detalhes sobre a API, execute a aplicação e **visite a documentação do Swagger** no endereço: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html#/)

### 1. /auth/register
- Endpoint inicial da aplicação para um novo usuário, responsável por receber requisições de cadastro de novos usuários

| Endpoint               | Descrição
|------------------------|-----------------------------------------------------
| <kbd>POST /auth/register</kbd>     | Cadastra um novo usuário

<br>

**ESTRUTURA PADRÃO PARA REQUISIÇÕES:**
```json
{
    "nome": "Carlos Silva", # Obrigatório
    "login": "carlos", # Obrigatório
    "email": "carlos@gmail.com", # Obrigatório
    "telefone": "(11) 11111-1111", # Obrigatório
    "senha": "Abc123@" # Obrigatório
}
```

**RESPOSTA ESPERADA (STATUS 201):**
```json
{
  "id": 2, # Definido automáticamente
  "nome": "Carlos Silva",
  "login": "carlos",
  "email": "carlos@gmail.com",
  "telefone": "(11) 11111-1111",
  "senha": "$2a$10$ehL1dRNv8HR8HJn0NnC9s.7heAo/05sVheHmo6SObdpJyDkjP8vkq",
  "role": "ROLE_USER" # Definido automaticamente
}
```

<br>

### 2. /auth/login
- Endpoint responsável por receber requisições de login de um usuário

| Endpoint               | Descrição
|------------------------|-----------------------------------------------------
| <kbd>POST /auth/login</kbd>     | Efetuar login de um usuário

<br>

**ESTRUTURA PADRÃO PARA REQUISIÇÕES:**
```json
{
    "login": "carlos",
    "senha": "Abc123@"
}
```

**RESPOSTA ESPERADA (STATUS 200):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjYXJsb3MiLCJpYXQiOjE3NzI1NDgxODMsImV4cCI6MTc3MjU0OTA4M30.x3C_SRv5YF1dh0qMXRUGd-G0GO36Ia1j7H_7xj7AAYianXNcTMooxT253FDseDqO9BCYvvuLoFMu88QoTk49UA"
}
```

<br>

### 3. /users/profile
- Endpoint responsável por retornar, atualizar ou deletar o usuário atualmente logado

| Endpoint               | Descrição
|------------------------|-----------------------------------------------------
| [<kbd>GET /users/profile</kbd>](#get-usersprofile)     | Retornar dados do usuário atual
| [<kbd>PATCH /users/profile</kbd>](#patch-usersprofile) | Atualizar dados do usuário atual (**permite alterar, no mínimo, um ou mais campos**)
| [<kbd>DELETE /users/profile</kbd>](#delete-usersprofile)  | Deletar o usuário atual

<br>

#### GET /users/profile

**RESPOSTA ESPERADA (STATUS 200):**
```json
{
  "id": 2,
  "nome": "Carlos Silva",
  "login": "carlos",
  "email": "carlos@gmail.com",
  "telefone": "(11) 11111-1111",
  "senha": "$2a$10$ehL1dRNv8HR8HJn0NnC9s.7heAo/05sVheHmo6SObdpJyDkjP8vkq",
  "role": "ROLE_USER"
}
```

#### PATCH /users/profile

**ESTRUTURA DE EXEMPLO PARA REQUISIÇÕES:**
```json
{
  "nome": "Carlos Carlos Silva Silva",
  "email": "carlossilva123@gmail.com",
  "telefone": "(99) 99999-9999"
}
```

**RESPOSTA ESPERADA (STATUS 200):**
```json
{
  "id": 2,
  "nome": "Carlos Carlos Silva Silva", # dado atualizado
  "login": "carlos",
  "email": "carlossilva123@gmail.com", # dado atualizado
  "telefone": "(99) 99999-9999", # dado atualizado
  "senha": "$2a$10$ehL1dRNv8HR8HJn0NnC9s.7heAo/05sVheHmo6SObdpJyDkjP8vkq",
  "role": "ROLE_USER"
}
```

#### DELETE /users/profile

**ATENÇÃO**: esse método só pode excluir usuários que não possuam vinculos com algum Pedido e retorna uma resposta com corpo vazio (Status 204 - No Content).


**RESPOSTA ESPERADA (STATUS 204):**
```json

```

<br>

### 4. /users/profile/orders
- Endpoint responsável por criar e retornar pedidos do usuário atualmente logado

| Endpoint               | Descrição
|------------------------|-----------------------------------------------------
| [<kbd>POST /users/profile/orders</kbd>](#post-usersprofileorders)     | Criar um pedido para o usuário atual
| <kbd>GET /users/profile/orders</kbd>      | Retornar todos os pedidos do usuário atual
| [<kbd>POST /users/profile/orders/{id}/payment</kbd>](#post-usersprofileordersidpayment)     | Criar um pagamento para um pedido do usuário atual
| [<kbd>POST /users/profile/orders/{id}/payment/confirm</kbd>](#post-usersprofileordersidpaymentconfirm)     | Confirmar o pagamento de um pedido do usuário atual
| [<kbd>POST /users/profile/orders/{id}/cancel</kbd>](#post-usersprofileordersidcancel)     | Cancelar um pedido do usuário atual

<br>

#### POST /users/profile/orders

**ESTRUTURA PADRÃO PARA REQUISIÇÕES:**
```json
{
  "items": [ # para criar um Pedido, deve-se preencher apenas os itens, os demais dados são gerados automaticamente
    {
      "productId": 4,
      "quantidade": 2
    },
    {
      "productId": 3,
      "quantidade": 1
    }
  ]
}
```

**RESPOSTA ESPERADA (STATUS 201):**
```json
{
  "id": 1, # Dados do Pedido
  "moment": "2026-03-03T15:22:36Z",
  "orderStatus": "AGUARDANDO_PAGAMENTO",
  "client": { # Dados do Usuário
    "id": 3,
    "nome": "Carlos Silva",
    "login": "carlos",
    "email": "carlos@gmail.com",
    "telefone": "(11) 11111-1111",
    "senha": "$2a$10$zNUlwLorSQelJZa0ig8hVOXEtTaKan/6JWlT7rtzhu./QojDSwy7.",
    "role": "ROLE_USER"
  },
  "items": [ # Itens do pedido (Produtos)
    {
      "productId": 3,
      "quantidade": 1,
      "preco": 6599.9,
      "subtotal": 6599.9, # Calculado automaticamente com base no Preço * Quantidade
      "product": {
        "id": 3,
        "nome": "Apple iPad M4",
        "descricao": "Lorem ipsum dolor sit amet...",
        "preco": 6599.9,
        "imgUrl": "https://cdn.awsli.com.br/800x800/284/284108/produto/298522188/ipad-pro-11-preto-u8w5xzl2m8.jpeg",
        "categories": [ # Categorias do Item
          {
            "id": 2,
            "nome": "Tablets"
          }
        ]
      }
    },
    {
      "productId": 4,
      "quantidade": 2,
      "preco": 345.9,
      "subtotal": 691.8,
      "product": {
        "id": 4,
        "nome": "Fone intrauricular HyperX Cloud II",
        "descricao": "Lorem ipsum dolor sit amet...",
        "preco": 345.9,
        "imgUrl": "https://images.kabum.com.br/produtos/fotos/483041/fone-de-ouvido-gamer-hyperx-cloud-earbuds-ii-com-microfone-vermelho-705l8aa_1701173988_gg.jpg",
        "categories": [
          {
            "id": 3,
            "nome": "Fones"
          },
          {
            "id": 4,
            "nome": "Acessórios"
          }
        ]
      }
    }
  ],
  "total": 7291.7,
  "payment": null # Dados do Pagamento, null por padrão
}
```

#### POST /users/profile/orders/{id}/payment

**RESPOSTA ESPERADA (STATUS 200)**

```json
{
  "id": 1, # Dados do Pagamento
  "moment": "2026-03-03T15:32:49Z",
  "order": { # Dados do Pedido
    "id": 1,
    "moment": "2026-03-03T15:22:36Z",
    "orderStatus": "PROCESSANDO_PAGAMENTO", # Status do Pedido alterado automaticamente
    "client": { # Dados do Usuário
      "id": 3,
      "nome": "Carlos Silva",
      "login": "carlos",
      "email": "carlos@gmail.com",
      "telefone": "(11) 11111-1111",
      "senha": "$2a$10$zNUlwLorSQelJZa0ig8hVOXEtTaKan/6JWlT7rtzhu./QojDSwy7.",
      "role": "ROLE_USER"
    },
    "items": [ # Itens do Pedido (Produtos)
      {
        "productId": 4,
        "quantidade": 2,
        "preco": 345.9,
        "subtotal": 691.8,
        "product": {
          "id": 4,
          "nome": "Fone intrauricular HyperX Cloud II",
          "descricao": "Lorem ipsum dolor sit amet...",
          "preco": 345.9,
          "imgUrl": "https://images.kabum.com.br/produtos/fotos/483041/fone-de-ouvido-gamer-hyperx-cloud-earbuds-ii-com-microfone-vermelho-705l8aa_1701173988_gg.jpg",
          "categories": [ # Categorias do Produto
            {
              "id": 3,
              "nome": "Fones"
            },
            {
              "id": 4,
              "nome": "Acessórios"
            }
          ]
        }
      },
      {
        "productId": 3,
        "quantidade": 1,
        "preco": 6599.9,
        "subtotal": 6599.9,
        "product": {
          "id": 3,
          "nome": "Apple iPad M4",
          "descricao": "Lorem ipsum dolor sit amet...",
          "preco": 6599.9,
          "imgUrl": "https://cdn.awsli.com.br/800x800/284/284108/produto/298522188/ipad-pro-11-preto-u8w5xzl2m8.jpeg",
          "categories": [
            {
              "id": 2,
              "nome": "Tablets"
            }
          ]
        }
      }
    ],
    "total": 7291.7,
    "payment": { # Objeto Pagamento criado e vinculado ao Pedido
      "id": 1,
      "moment": "2026-03-03T15:32:49Z",
      "status": "PENDENTE"
    }
  },
  "status": "PENDENTE",
  "pixQrCode": "00020126580014BR.GOV.BCB.PIX0136123e4567-e89b-12d3-a456-42661417400052040000530398654067291.705802BR5901%6001%62070503***6304MINHA_LOJA2595", # Código gerado para alimentar frontend
  "pixCopiaCola": "00020126580014BR.GOV.BCB.PIX0136123e4567-e89b-12d3-a456-42661417400052040000530398654067291.705802BR5901%6001%62070503***6304MINHA_LOJA2595" # Código gerado para alimentar frontend
}
```

#### POST /users/profile/orders/{id}/payment/confirm

**RESPOSTA ESPERADA (STATUS 200)**

```json
{
  "id": 1,
  "moment": "2026-03-03T15:22:36Z",
  "orderStatus": "PAGO", # Status do Pedido alterado automaticamente
  "client": {
    "id": 3,
    "nome": "Carlos Silva",
    "login": "carlos",
    "email": "carlos@gmail.com",
    "telefone": "(11) 11111-1111",
    "senha": "$2a$10$zNUlwLorSQelJZa0ig8hVOXEtTaKan/6JWlT7rtzhu./QojDSwy7.",
    "role": "ROLE_USER"
  },
  "items": [
    {
      "productId": 3,
      "quantidade": 1,
      "preco": 6599.9,
      "subtotal": 6599.9,
      "product": {
        "id": 3,
        "nome": "Apple iPad M4",
        "descricao": "Lorem ipsum dolor sit amet...",
        "preco": 6599.9,
        "imgUrl": "https://cdn.awsli.com.br/800x800/284/284108/produto/298522188/ipad-pro-11-preto-u8w5xzl2m8.jpeg",
        "categories": [
          {
            "id": 2,
            "nome": "Tablets"
          }
        ]
      }
    },
    {
      "productId": 4,
      "quantidade": 2,
      "preco": 345.9,
      "subtotal": 691.8,
      "product": {
        "id": 4,
        "nome": "Fone intrauricular HyperX Cloud II",
        "descricao": "Lorem ipsum dolor sit amet...",
        "preco": 345.9,
        "imgUrl": "https://images.kabum.com.br/produtos/fotos/483041/fone-de-ouvido-gamer-hyperx-cloud-earbuds-ii-com-microfone-vermelho-705l8aa_1701173988_gg.jpg",
        "categories": [
          {
            "id": 3,
            "nome": "Fones"
          },
          {
            "id": 4,
            "nome": "Acessórios"
          }
        ]
      }
    }
  ],
  "total": 7291.7,
  "payment": { # Dados do Pagamento vinculado ao Pedido
    "id": 1,
    "moment": "2026-03-03T15:32:49Z",
    "status": "PAGO" # Status do Pagamento alterado automaticamente
  }
}
```

#### POST /users/profile/orders/{id}/cancel

**RESPOSTA ESPERADA (STATUS 200)**

```json
{
  "id": 2, # Dados do Pedido
  "moment": "2026-03-03T16:02:03Z",
  "orderStatus": "CANCELADO", # Status do Pedido alterado automaticamente
  "client": {
    "id": 3,
    "nome": "Carlos Silva",
    "login": "carlos",
    "email": "carlos@gmail.com",
    "telefone": "(11) 11111-1111",
    "senha": "$2a$10$zNUlwLorSQelJZa0ig8hVOXEtTaKan/6JWlT7rtzhu./QojDSwy7.",
    "role": "ROLE_USER"
  },
  "items": [
    {
      "productId": 9,
      "quantidade": 2,
      "preco": 529.9,
      "subtotal": 1059.8,
      "product": {
        "id": 9,
        "nome": "Estação de Carga UGREEN NEXODE",
        "descricao": "Lorem ipsum dolor sit amet...",
        "preco": 529.9,
        "imgUrl": "https://m.media-amazon.com/images/I/51KyN6BQP-L.jpg",
        "categories": [
          {
            "id": 4,
            "nome": "Acessórios"
          }
        ]
      }
    }
  ],
  "total": 1059.8,
  "payment": null # Dados do Pagamento, null por padrão caso o Pedido seja cancelado antes de iniciar um Pagamento
}
```

<br>

## CONTRIBUIR PARA O PROJETO

Caso queira contribuir de alguma forma para o projeto, sinta-se a vontade para seguir esses passos:

1. Clone esse repositório para a sua máquina utilizando o comando: `git clone git@github.com:CarlosSilva-DEV/projeto-integrador-web-app.git`
2. Crie uma branch específica para promover suas alterações.
3. Abra um Pull Request neste repositório explicando sobre as alterações propostas. Em caso de alterações visuais da aplicação, anexe capturas de telas e aguarde a revisão.

### Documentações auxiliares:

[📝 Como criar um Pull Request?](https://www.atlassian.com/br/git/tutorials/making-a-pull-request)

[💾 Padrões de commits](https://gist.github.com/joshbuchea/6f47e86d2510bce28f8e7f42ae84c716)