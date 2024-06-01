(ns core.ports.driver.interface-api)


(defprotocol IAluno
  (valida-dados-cadastro [this nome-aluno documento-aluno])
  (valida-dados-atualizacao [this nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc]))