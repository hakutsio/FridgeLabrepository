# Fridge Lab — revisão de segurança

Sistema didático para cadastrar e configurar uma frota de geladeiras simuladas, usado em práticas de Segurança da Informação. Teste educacional.

Tecnologias: Spring Boot, PostgreSQL, Flyway, React e um simulador de firmware em contêiner separado.

## Execução local

1. Copie o arquivo de exemplo e **troque todas as senhas**:

   ```bash
   cp .env.example .env
   ```

2. Suba o laboratório:

   ```bash
   docker compose up --build
   ```

Interface: http://localhost:3002 · API: http://localhost:8082

O `.env` está no `.gitignore`: nenhuma senha real fica no repositório. Se alguma variável estiver faltando, o Compose avisa e não sobe.

Se o banco já foi criado antes com outras senhas, recrie-o com `docker compose down -v` antes de subir de novo. O mesmo comando restaura os dados de exemplo.

## Login e perfis

O site abre com uma tela de login. Existem dois usuários, com perfis diferentes:

| Usuário | Perfil | Senha | Geladeiras |
|---|---|---|---|
| `teacher` | Professor (`TEACHER`) | `TEACHER_PASSWORD` do `.env` | 12 (IDs 1, 2 e 4 a 13) |
| `student` | Aluno (`STUDENT`) | `STUDENT_PASSWORD` do `.env` | 8 (IDs 3 e 14 a 20) |

Cada usuário vê e altera apenas as próprias geladeiras. Só o professor acessa a documentação Swagger.

As senhas ficam guardadas na API com BCrypt. O site não tem nenhuma senha no código; ele usa apenas o que a pessoa digita no login.

## Documentação da API (Swagger)

O Swagger fica **desligado por padrão**. Para usar em desenvolvimento, coloque `SWAGGER_ENABLED=true` no `.env` e acesse http://localhost:8082/swagger-ui/index.html com o login do professor. O Swagger não é repassado pelo site (porta 3002).

## Acesso pelo DBeaver

O PostgreSQL é publicado somente em `127.0.0.1`.

| Campo | Valor |
|---|---|
| Host | `localhost` |
| Port | `5432` |
| Database | `geladeira_lab` |
| Username | `postgres` (administrador) ou `fridge_app` (conta do sistema) |
| Password | `POSTGRES_PASSWORD` ou `DB_PASSWORD` do `.env` |

A conta `fridge_app` tem `SELECT`, `INSERT`, `UPDATE` e `DELETE` apenas na tabela `fridge_devices` (as duas últimas foram adicionadas na migration V5). Use `postgres` só para inspecionar migrations e o schema.

A migration V6 troca a senha de `fridge_app` pelo valor de `DB_PASSWORD`, porque a senha antiga estava escrita na V2 e continua no histórico do Git.

## Rotas principais

Todas exigem login, exceto `/` e `/api`, que só mostram o status da API.

| Método | Rota | Uso |
|---|---|---|
| GET | `/api/me` | Usuário logado e perfil (usado pela tela de login) |
| GET | `/api/devices?name=Hub` | Listar e buscar geladeiras do usuário |
| GET | `/api/devices/{id}` | Consultar uma geladeira |
| POST | `/api/devices` ou `/api/devices/register` | Cadastrar uma geladeira |
| PUT | `/api/devices/{id}` | Editar dados, localização e firmware |
| DELETE | `/api/devices/{id}` | Excluir uma geladeira do usuário |
| PUT | `/api/devices/{id}/configuration` | Enviar temperatura, modo e nota ao firmware |
| PUT | `/api/devices/{id}/firmware` | Salvar código C++ do firmware didático |
| GET | `/api/firmware/state` | Consultar estado aplicado no simulador |

Exemplo para cadastrar:

```json
{
  "name": "Câmara de treinamento",
  "temperatureC": 4,
  "mode": "ECO",
  "note": "Amostras fictícias",
  "country": "Brasil",
  "city": "São Paulo",
  "latitude": -23.55052,
  "longitude": -46.633308,
  "firmwareCode": "void setup() { pinMode(LED_BUILTIN, OUTPUT); }\nvoid loop() { readTemperature(); maintainTarget(); delay(1000); }"
}
```

Exemplo para aplicar uma configuração no firmware:

```json
{
  "temperatureC": 2,
  "mode": "ECO",
  "note": "Configuração de laboratório"
}
```

O banco é populado pela migration `V4__seed_twenty_fridges.sql` com 20 geladeiras fictícias. O frontend tem telas de visão geral/mapa, lista e CRUD, cadastro e laboratório de firmware.

## Limitações conhecidas

- O simulador de firmware (porta 8081, rede interna do Docker) não exige autenticação.
- Não há limite de tentativas de login nem HTTPS; use apenas em ambiente local.
