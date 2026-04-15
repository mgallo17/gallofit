# language: pt
Funcionalidade: Metas dinâmicas por tipo de dia
  Como utilizador do GalloFit
  Quero que as minhas metas se ajustem automaticamente
  Consoante o tipo de dia (normal ou duplo)

  Esquema do cenário: Meta correcta por dia da semana
    Dado que hoje é <dia>
    Então a meta calórica deve ser <meta> kcal
    E o tipo de dia deve ser "<tipo>"

    Exemplos:
      | dia      | meta | tipo   |
      | segunda  | 2100 | NORMAL |
      | terça    | 2200 | DUPLO  |
      | quarta   | 2100 | NORMAL |
      | quinta   | 2200 | DUPLO  |
      | sexta    | 2100 | NORMAL |
      | sábado   | 2100 | NORMAL |
      | domingo  | 2100 | NORMAL |

  Cenário: Treino registado no Health Connect actualiza calorias
    Dado que o utilizador fez 50 minutos de ginásio
    E o Health Connect reporta 220 calorias queimadas
    Quando o sistema sincroniza com o Health Connect
    Então as calorias líquidas do dia devem considerar o treino
    E as calorias restantes devem aumentar em 220
