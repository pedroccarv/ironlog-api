# 🏋️‍♂️ IronLog API
O **IronLog** é uma solução Full-Stack completa desenvolvida para entusiastas de musculação que buscam otimizar os seus treinos de hipertrofia. A aplicação permite o gerenciamento detalhado de treinos, exercícios e séries, garantindo o acompanhamento preciso da progressão de carga e volume.

Este projeto reflete os meus estudos práticos em Engenharia de Software na PUC Minas, com foco na construção de uma arquitetura limpa, segura e escalável utilizando o ecossistema Java.

## 🎯 Objetivos do Projeto
- **Controle de Hipertrofia:** Focado em usuários que seguem planos de alta performance e precisam monitorar volume e carga (ideal para treinos de baixo volume e alta intensidade).
- **Arquitetura Moderna:** Implementação de uma API RESTful utilizando as melhores práticas do Spring Boot.
- **Segurança Robusta:** Autenticação e autorização via JWT (JSON Web Tokens) com senhas criptografadas.

## 🛠️ Tecnologias Utilizadas

### Backend (Core)
- **Java 21:** Utilizando os recursos mais recentes da linguagem, como `Records` para DTOs.
- **Spring Boot 4.0.2:** Base da aplicação para facilitar o desenvolvimento e configuração.
- **Spring Security:** Proteção das rotas e controle de acesso.
- **JWT (Auth0):** Geração e validação de tokens para autenticação Stateless.
- **Spring Data JPA & Hibernate:** Persistência de dados e mapeamento objeto-relacional.
- **PostgreSQL:** Banco de dados relacional robusto para armazenamento seguro.

### Frontend
- **React + TypeScript:** Interface reativa, moderna e fortemente tipada.
- **Axios:** Integração com o backend via interceptors para o gerenciamento automático de tokens.
- **Tailwind CSS:** Estilização moderna e responsiva.

## 🔐 Segurança e Autenticação
A aplicação implementa um fluxo de segurança rigoroso:
1. **BCrypt:** Todas as senhas são encriptadas antes de chegarem ao banco de dados.
2. **Stateless Auth:** O servidor não armazena sessões na memória, utilizando `SessionCreationPolicy.STATELESS` para maior escalabilidade.
3. **Filtros Personalizados:** Um `SecurityFilter` intercepta cada requisição para validar o crachá digital (Token JWT) do usuário antes de liberar o acesso aos dados.

## 🚀 Funcionalidades Principais
- **Registro e Login:** Sistema de criação de conta e acesso seguro.
- **Gestão de Treinos:** CRUD completo de treinos vinculados ao usuário logado.
- **Séries e Exercícios:** Registro detalhado de séries (sets), repetições e peso.
- **Recordes Pessoais (PR):** Endpoint específico para consultar a carga máxima já levantada em cada exercício.

## 📦 Como rodar o projeto localmente

### Pré-requisitos
- JDK 21
- Maven 3.9+
- PostgreSQL

### Configuração
No arquivo `src/main/resources/application.properties`, configure as suas variáveis de ambiente ou substitua os valores:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ironlog
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
api.security.token.secret=sua_chave_secreta_jwt
```

## Execução

```bash
# Clone o repositório
git clone https://github.com/pedroccarv/ironlog-api.git

# Execute o backend usando o Maven Wrapper
./mvnw spring-boot:run
```
