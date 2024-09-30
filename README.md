<h1 align="center">DNS Resolver</h1>

<p align="center">
  <a><img alt="jdk" src="https://img.shields.io/badge/JDK%2021-007396?style=for-the-badge&logo=openjdk&logoColor=white"></a>
  <a><img alt="redis" src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"></a>
</p>

<p align="center">DNS Resolver rápido e privado.</p>

## 📜 Sumário

- [📋 Sobre o Projeto](#-sobre-o-projeto)
- [💡 Funcionalidades](#-funcionalidades)
- [📦 Pré-requisitos](#-pré-requisitos)
- [🔧 Configurando o Projeto](#-configurando-o-projeto)
  - [Usando Docker](#usando-docker)
- [👤 Autor](#-autor)
- [⚖️ Licença](#-licença)

## 📋 Sobre o Projeto

> [!TIP]
> Este projeto visa ser um estudo prático sobre o funcionamento do DNS e sua implementação. Apesar disso, ele também é uma solução leve e funcional para um DNS Resolver self-hosted.

## 💡 Funcionalidades

- [ ] EDNS
- [x] Consultas UDP
- [ ] Resolução de Consultas UDP usando DoH (Cloudflare)
- [ ] Resolução de Consultas UDP usando TCP
- [ ] Consultas TCP
- [ ] Consulta Recursiva

- Tipos de Consultas
  - [x] A
  - [ ] NS
  - [ ] CNAME
  - [ ] MX
  - [ ] AAAA
  - [ ] SOA

- Configurações
    - Cache
      - [ ] Cache por arquivo
      - [ ] Cache usando Redis
      - [ ] Cache em memória
      - [ ] Sem cache
    - Servidor
      - [x] UDP (IPv4)
      - [x] UDP (IPv6)
      - [ ] TCP (IPv4)
      - [ ] TCP (IPv6)
    - NameServers
      - [ ] Lista de Nameservers personalizada
      - [ ] Nameserver aleatório
    - [ ] Lista de Nomes proibidos

## 📦 Pré-requisitos

Antes de começar, verifique se você possui as seguintes ferramentas instaladas:

- **[JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)** ou superior
- Gerenciador de dependências **[Maven](https://maven.apache.org/install.html)**
- Um editor de código recomendado: **[IntelliJ IDEA](https://www.jetbrains.com/idea/download/)** (ou outro de sua preferência)

## 🔧 Configurando o Projeto

Siga os passos abaixo para configurar e executar o projeto:

### Usando Docker

Se preferir pode configurar o projeto usando o docker, desta forma:

## 👤 Autor

Feito por [cybermumuca](https://github.com/cybermumuca). Se você tiver alguma dúvida ou sugestão, sinta-se à vontade para entrar em contato!

## ⚖️ Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](./LICENSE) para mais detalhes.