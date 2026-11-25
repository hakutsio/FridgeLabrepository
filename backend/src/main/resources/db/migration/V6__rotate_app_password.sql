-- A senha da conta fridge_app estava escrita na V2 (e continua no histórico do Git).
-- Aqui ela é trocada pelo valor de DB_PASSWORD, que vem do arquivo .env e nunca fica no código.
-- Observação: desde a V5 essa conta também pode inserir e excluir dispositivos (SELECT, INSERT, UPDATE, DELETE).
ALTER ROLE fridge_app WITH PASSWORD '${appDbPassword}';
