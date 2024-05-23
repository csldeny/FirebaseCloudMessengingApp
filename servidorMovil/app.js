const express = require('express');
const bodyParser = require('body-parser');
const admin = require('firebase-admin');
const path = require('path');
const fs = require('fs');

// Ruta de la key de firebase
const serviceAccount = require('./fir-cloud-messenging-3c893-firebase-adminsdk-afnxb-64699df550');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const app = express();
const port = 3000;

app.use(bodyParser.json());

app.get('/', (req, res) => {
  const indexPath = path.join(__dirname, 'index.html');
  res.sendFile(indexPath);
});

app.post('/send-notification', (req, res) => {
  const { token, title, body } = req.body;

  if (!token || !title || !body) {
    return res.status(400).send('Token, Titulo y cuerpo son requeridos.');
  }

  const message = {
    notification: {
      title: title,
      body: body,
    },
    token: token,
  };

  admin
    .messaging()
    .send(message)
    .then((response) => {
      console.log('Mensaje enviado correctamente:', response);
      res.status(200).send('Mensaje enviado correctamente');
    })
    .catch((error) => {
      console.log('Error al enviar el mensaje:', error);
      res.status(500).send('Error al enviar el mensaje');
    });
});

app.listen(port, () => {
  console.log(`El servidor se está ejecutando en http://localhost:${port}`);
});
