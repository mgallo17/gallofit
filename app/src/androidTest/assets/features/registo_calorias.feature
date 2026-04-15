# language: pt
Funcionalidade: Registo diário de calorias
  Como utilizador do GalloFit
  Quero registar as minhas refeições
  Para acompanhar as minhas calorias diárias

  Contexto:
    Dado que o utilizador tem uma meta de 2100 kcal para dias normais
    E uma meta de 2200 kcal para dias duplos (terça e quinta)

  Cenário: Dia normal dentro da meta
    Dado que hoje é segunda-feira
    Quando o utilizador regista as seguintes refeições:
      | nome                  | kcal |
      | Iogurte + Morangos    | 110  |
      | Carne + Batata + Ovos | 530  |
      | Jantar igual          | 530  |
      | Whey Padrão           | 390  |
    Então o total consumido deve ser 1560 kcal
    E as calorias restantes devem ser 540 kcal
    E o estado deve ser "dentro da meta"

  Cenário: Dia duplo (terça) com ténis
    Dado que hoje é terça-feira
    Quando o utilizador regista as seguintes refeições:
      | nome                  | kcal |
      | Iogurte + Morangos    | 110  |
      | Carne + Batata + Ovos | 530  |
      | Jantar igual          | 530  |
      | Whey Padrão           | 390  |
      | Banana extra          | 105  |
    Então o total consumido deve ser 1665 kcal
    E as calorias restantes devem ser 535 kcal
    E o tipo de dia deve ser "DUPLO"

  Cenário: Meta excedida
    Dado que hoje é segunda-feira
    Quando o utilizador regista 2500 kcal no total
    Então o total consumido deve ser 2500 kcal
    E as calorias restantes devem ser -400 kcal
    E o estado deve ser "meta excedida"

  Cenário: Usar template Whey Padrão
    Dado que o utilizador tem o template "Whey Padrão" guardado
    Quando o utilizador aplica o template ao slot "Pós-treino"
    Então deve ser adicionada uma entrada com 390 kcal
    E a proteína deve ser 34 gramas
    E o slot deve ser "POST_WORKOUT"
