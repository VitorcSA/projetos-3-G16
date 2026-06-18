# Calculadora de Emissões CO₂ — Sintropia (G16)

Aplicação web para calcular e comparar as emissões de carbono geradas pelo uso de **cartões físicos** versus **cartões digitais** em empresas. O sistema permite que empresas estimem seu impacto ambiental com base no número de colaboradores e localização, acompanhem metas ao longo do tempo e exportem relatórios.

---

## Funcionalidades

- **Cálculo de emissões**: estima as emissões anuais de CO₂ de cartões físicos (produção em PVC, transporte e transações) e compara com a alternativa digital
- **Dashboard de emissão prevista**: exibe o resultado do cálculo com breakdown por categoria (produção, transporte, transações)
- **Histórico e metas**: acompanhamento temporal das emissões e definição de metas de redução
- **Exportação de relatórios**: exporta dashboards nos formatos PDF e Excel (.xlsx)
- **Autenticação com JWT**: login seguro via cookie, com proteção de rotas por Spring Security
- **Perfil de usuário**: cadastro e edição de dados da empresa (nome, e-mail, número de colaboradores, endereço)
- **Integração com APIs externas**:
  - **Climatiq**: estimativa de emissões via API
  - **OpenRouteService**: cálculo de distância até a fábrica de cartões mais próxima para estimar emissão de transporte

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Backend | Java 17, Spring Boot 4.0.6 |
| Persistência | Spring Data JPA, MySQL 8.0 |
| Segurança | Spring Security, JWT (jjwt 0.12.6) |
| Frontend | Thymeleaf, HTML/CSS/JS |
| Exportação | iTextPDF 7.2.5, Apache POI 5.2.5 |
| Build | Maven |
| Infraestrutura | Docker, Docker Compose |

---

## Pré-requisitos

- Java 17+
- Maven 3.9+
- Docker e Docker Compose (recomendado para o banco de dados)

---

## Como executar

### 1. Subir o banco de dados com Docker

```bash
cd calculator
docker-compose up -d
```

Isso cria um container MySQL na porta `3306` com o banco `sintropia`.

### 2. Configurar as variáveis de ambiente

Edite o arquivo `src/main/resources/application.properties` com suas credenciais:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sintropia
spring.datasource.username=root
spring.datasource.password=root

climatiq.api.key=SUA_CHAVE_CLIMATIQ
openrouteservice.api.key=SUA_CHAVE_ORS

jwt.secret=SEU_SEGREDO_JWT
contact.email=seu@email.com
```

> **Atenção:** nunca suba o arquivo `application.properties` com chaves reais para o repositório. Use variáveis de ambiente ou um arquivo `.env` ignorado pelo `.gitignore`.

### 3. Executar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

### 4. Executar via Docker (build completo)

```bash
docker build -t sintropia-calculator .
docker run -p 8080:8080 sintropia-calculator
```

---

## Estrutura do projeto

```
calculator/
├── src/main/java/com/sintropia/calculator/
│   ├── config/          # Configuração de segurança (Spring Security)
│   ├── controller/      # Controllers REST e de páginas (Thymeleaf)
│   ├── dto/             # Objetos de transferência de dados (request/response)
│   ├── exception/       # Exceções de negócio customizadas
│   ├── filter/          # Filtro JWT para autenticação
│   ├── mapper/          # Conversores entre entidades e DTOs
│   ├── model/           # Entidades JPA (User, MonthlyRecord, Address...)
│   ├── repository/      # Interfaces Spring Data JPA
│   ├── service/         # Lógica de negócio (cálculo, auth, usuário, exportação)
│   └── strategy/        # Estratégias de exportação (PDF, Excel)
├── src/main/resources/
│   ├── static/          # CSS, JS e imagens
│   └── templates/       # Templates Thymeleaf (index, login, register, history)
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## Lógica de cálculo de emissões

O cálculo é feito pelo `CalculatorService` com base em fatores de emissão de fontes científicas e governamentais:

| Fator | Valor | Fonte |
|---|---|---|
| Peso do cartão físico | 5g | Payments Dive |
| Emissão PVC (produção) | 2,7 kgCO₂/kg | ecoinvent |
| Fator de transporte | 0,21 kgCO₂/t·km | Gov. UK |
| Fator energético (produção) | 0,084 kgCO₂/cartão | MCTI Brasil |
| Emissão cartão digital | 0,01 kgCO₂/cartão | TerraGreetings |
| Emissão por transação | 0,00085 kgCO₂ | DNB/Netherlands |

A distância até a fábrica de cartões mais próxima é calculada dinamicamente via **OpenRouteService**, com base no endereço da empresa cadastrada.

---

## API REST

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Cadastro de novo usuário | Pública |
| POST | `/api/auth/login` | Login, retorna JWT via cookie | Pública |
| GET | `/api/auth/validate` | Valida token JWT | Pública |
| GET | `/api/user/profile` | Retorna perfil do usuário logado | Privada |
| PUT | `/api/user/profile` | Atualiza perfil do usuário | Privada |
| GET | `/api/dashboards` | Lista dashboards disponíveis | Privada |
| GET | `/api/dashboards/{id}/export?format=pdf\|xlsx` | Exporta dashboard | Privada |
| GET | `/api/records` | Lista registros mensais | Privada |

---

##  Equipe

**Ciência da Computação:**
- [Vitor Sá](https://github.com/VitorcSA)
- [Guilherme Hecksher](https://github.com/G-Hecksher8)
- [Maria Júlia Ferreira](https://github.com/Majufponte)
- [Matheus Chaves](github.com)
- [Guilherme Lacerda](https://github.com/guilhermeblacerda)
- [Natan Tavares](https://github.com/Natan-Tavares)
  
**Design:**
- Loreena Romão
- Sophia Louise
- Isabela Accioly
- Giovani Thiago
