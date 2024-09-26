# DNS Resolver

## Funcionalidades

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
