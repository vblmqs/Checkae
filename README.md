# Checkaê – App Android de Gestão de Tarefas

Aplicativo Android desenvolvido para facilitar o gerenciamento de tarefas e subtarefas do dia a dia, com foco em produtividade, organização e usabilidade.

---

## Objetivo

Este projeto foi desenvolvido como trabalho prático da disciplina **DS151 - Desenvolvimento para Dispositivos Móveis**, com os seguintes objetivos:

- Aplicar a arquitetura MVVM na prática
- Integrar Firebase para autenticação e persistência de dados
- Construir uma interface moderna com Jetpack Compose
- Utilizar notificações para alertar usuários sobre prazos de tarefas

---

## Integrantes do Grupo

- **Joyce Adriana Borecki**
- **Victoria Isabele Corbolin Monte**

---

## Como Executar

Você pode rodar o Checkaê de duas formas: no emulador do Android Studio ou diretamente em um celular Android.

### Requisitos

- Android Studio
- Dispositivo físico

### Passos para execução

1. Clone o repositório:

   ```bash
   git clone https://gitlab.com/ds151-2025-1-t-grr20221100/ds151-project-2025-1.git
   ```

2. Abra o projeto no Android Studio

3. Faça checkout da branch principal:

   ```bash
   git checkout agrupar
   ```

4. Execute o app:
    - Via emulador: Utilize o AVD Manager do Android Studio e clique em Run app ou diretamente via atalho por Shift+F10
    - Via celular: Conecte um dispositivo Android com depuração USB ativada e clique em Run app ou diretamente via atalho por Shift+F10

> O Firebase já está totalmente configurado — basta rodar o app e utilizar normalmente.

---

## Funcionalidades

### Autenticação
- Login e registro de usuários com Firebase Authentication

### Tarefas e Subtarefas
- Criação, edição e exclusão de tarefas
- Subtarefas com prazo e status próprios
- Status disponíveis: Iniciada, Pausada e Concluída
- Prioridades disponíveis: Alta, Média e Baixa

### Notificações
- Notificações automáticas no dia do vencimento de tarefas e subtarefas iniciadas
- Implementadas com WorkManager

### Tema Claro/Escuro
- Alternância entre tema claro e escuro usando DataStore

### Busca, Filtros e Ordenação
- Busca por título de tarefas
- Filtro por prioridade e status
- Ordenação por data limite (urgência)

---

## Tecnologias Utilizadas

- Kotlin
- Jetpack Compose + Material 3
- Firebase Authentication
- Firebase Firestore
- WorkManager
- DataStore Preferences
- Arquitetura MVVM