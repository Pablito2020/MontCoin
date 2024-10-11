<h1 align="center"> MontCoin 💸 💹 </h1>
MontCoin is an app that allows users to simulate and track imaginary transactions using NFC-enabled smartbands. It was created for a summer camp where I was an instructor, with the goal of giving teens a fun, hands-on experience to learn about money management in a playful setting.

## 🚧 Disclaimer
This app is a prototype intended for personal use during a one-week camp, so it's not production-ready. It has several known limitations, including: 

  - We’re using an ORM, but none of the operations are optimized, which results in slow performance.
  - Certificates are stored without passwords, as the code to handle password protection hasn’t been implemented yet.
  - On Android, certificates are stored in raw format within the APK, meaning they could be extracted if the APK is decompiled. Ideally, the Android Keystore System should have been used for secure storage

