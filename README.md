# HealthSmartApp 🏃‍♂️📱

Aplicativo acadêmico desenvolvido para monitoramento de atividades físicas e incentivo à prática de exercícios diários.

## 🎯 Proposta

O **HealthSmartApp** tem como objetivo ajudar usuários a se manterem ativos por meio do controle de treinos com sensores do próprio dispositivo, exibindo métricas úteis como passos dados, tempo em atividade e estimativa de calorias queimadas.

## 🧠 Funcionalidades

- Autenticação via e-mail com Firebase Auth
- Contador de passos utilizando o sensor `Step Counter`
- Início de treino por gesto (giro com o giroscópio) ou botão manual
- Temporizador com pausa e finalização
- Salvamento de treinos no Firebase Firestore
- Tela Home com estatísticas: total de treinos, passos, tempo e calorias
- Logout e controle de sessão

## 🧰 Tecnologias e Recursos Utilizados

- 👣 **Sensores Android**:  
  - `Step Counter` para contar passos  
  - `Gyroscope` para iniciar treino com movimento

- ☁️ **Firebase**:  
  - `Authentication`  
  - `Firestore` (salvamento de treinos)

- 📱 **Kotlin 2.0.0**
- 🎯 SDK mínimo: 29

## 📌 Observações

Este app está em fase inicial de desenvolvimento com fins acadêmicos. Novas funcionalidades e melhorias serão implementadas futuramente.
