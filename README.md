# NetworkChatServer

Консольное приложение сетевой чат (серверная часть):
1. клиенту при подключении предлагается ввести свое имя, недопустимо вводить имя 'admin', остальное можно
2. на каждого клиента создает два потока: 
   первый - читает последние сообщения в чате и передает клиенту, 
   второй - читает сообщения от клиента и добавляет их в чат
3. при входе нового пользователя или выходе, сообщения об этом пишет специальный пользователь 'admin'
4. для выхода надо сообщить "/exit"




логирование
1. программа ведет запись событий в лог файл "log.txt", сохраняемый в каталоге проекта