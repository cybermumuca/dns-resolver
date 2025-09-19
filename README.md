<h1 align="center">🚧 DNS Resolver 🚧</h1>

<p align="center">
  <a><img alt="jdk" src="https://img.shields.io/badge/JDK%2021-007396?style=for-the-badge&logo=openjdk&logoColor=white"></a>
  <a><img alt="redis" src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"></a>
</p>

<p align="center">DNS Resolver rápido e privado.</p>
<!-- <p align="center">⚠️ <strong>Em desenvolvimento</strong> ⚠️</p> -->

## 📜 Sumário

- [📋 Sobre o Projeto](#-sobre-o-projeto)
- [📚 Introdução ao DNS]()
  - [Oque é DNS]() 
  - [Como o DNS funciona?]() 
  - [Tipos de Registros DNS]()
- [⚠️ Ataques ao DNS]()
  - [DNS Spoofing (Cache Poisoning)]() 
  - [Ataques DDoS em DNS]() 
  - [Amplificação de DNS]() 
- [💡 Funcionalidades](#-funcionalidades)
- [📦 Pré-requisitos](#-pré-requisitos)
- [🔧 Configurando o Projeto](#-configurando-o-projeto)
  - [Manualmente](#manualmente)
  - [Usando Docker](#usando-docker)
- [👤 Autor](#-autor)
- [⚖️ Licença](#-licença)

## 📋 Sobre o Projeto

Este projeto tem dois objetivos principais: fornecer uma implementação prática de um **DNS Resolver** completamente funcional, rápido e privado, além de servir como um recurso educacional sobre o funcionamento do DNS e as ameaças relacionadas.

O projeto segue as especificações das seguintes RFCs:
- [RFC 1035](https://datatracker.ietf.org/doc/html/rfc1035), que define a implementação básica de um resolver DNS.
- [RFC 3425](https://datatracker.ietf.org/doc/html/rfc3425), que trata da descontinuação de consultas inversas pelo DNS.
- [RFC 3596](https://datatracker.ietf.org/doc/html/rfc3596), que adiciona suporte para registros DNS relacionados ao IPv6 (registro `AAAA`).
- [RFC 7766](https://datatracker.ietf.org/doc/html/rfc7766), que especifica o uso de DNS sobre TCP para consultas maiores.

Embora este projeto não implemente diretamente o protocolo **DNS over HTTPS (DoH)**, como definido na [RFC 8484](https://datatracker.ietf.org/doc/html/rfc8484), ele repassa consultas DNS para o servidor da **Cloudflare**, que utiliza DoH para melhorar a segurança e privacidade das resoluções DNS.

Essas RFCs formam a base teórica necessária para o desenvolvimento prático deste projeto.

> [!CAUTION]
> É **desencorajado** o uso dessa solução em ambientes que necessitam de alta disponibilidade e robustez.

## 💡 Funcionalidades

**⚠️OBS⚠️**: este projeto se encontra em estado de desenvolvimento ativo, algumas das funcionalidades planejadas não estão funcionando corretamente ou ainda não foram implementadas.

✅️: Funcionando corretamente.  
⚠️: Funcionando com alguns problemas.  
❌️: Não implementado.  

- ❌️ EDNS
- ✅️ Consultas UDP
- ✅️ Resolução de Consultas UDP usando DoH (Cloudflare)
- ❌️ Resolução de Consultas UDP usando TCP
- ❌️ Consultas TCP
- ⚠️ Consulta Recursiva

- Tipos de Consultas
  - ✅️ A
  - ✅️ NS
  - ✅️ CNAME
  - ✅️ MX
  - ✅️ AAAA
  - ✅️ SOA
  - ✅️ PTR
  - ❌️ HTTPS

- Blacklist para sites de:
  - ❌️ Jogos de Azar
  - ❌️ Pornôgrafia
  - ❌️ Redes sociais
  - ❌️ Rastreadores
  - ❌️ Anúncios
  - ❌️ Fake news
  - ❌️ Malwares

- ❌️ Renovação de Cache em segundo plano

- Configurações
    - Caches
      - ❌️ Cache por arquivo
      - ❌️ Cache usando Redis
      - ❌️ Cache em memória
      - ⚠️ Sem cache
    - Servidores
      - ✅️ UDP (IPv4)
      - ✅️ UDP (IPv6)
      - ❌️ TCP (IPv4)
      - ❌️ TCP (IPv6)
    - NameServers
      - ❌️ Lista de Nameservers personalizada
      - ❌️ Nameserver aleatório
    - Resolvers
      - ✅️ Cloudflare DoH Resolver
      - ✅️ Cloudflare DNS Resolver
      - ✅️ Google DNS Resolver
      - ⚠️ UDP DNS Resolver
      - ❌️ TCP DNS Resolver
    - BlackList
      - ❌️ Lista de Nomes proibidos

## 📦 Pré-requisitos

Antes de começar, verifique se você possui as seguintes ferramentas instaladas:

- **[JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)** ou superior
- Gerenciador de dependências **[Maven](https://maven.apache.org/install.html)**
- Um editor de código recomendado: **[IntelliJ IDEA](https://www.jetbrains.com/idea/download/)** (ou outro de sua preferência)

## 🔧 Configurando o Projeto

Configure e execute o projeto:

- [Manualmente](#manualmente)
- [Usando Docker](#usando-docker) (Recomendado)

### Manualmente

Siga os passos abaixo para configurar e executar o projeto:

### Usando Docker

Siga os passos abaixo para configurar um container docker e começar a usar o projeto:

## 👤 Autor

Feito por [cybermumuca](https://github.com/cybermumuca). Se você tiver alguma dúvida ou sugestão, sinta-se à vontade para entrar em contato!

## ⚖️ Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](./LICENSE) para mais detalhes.
