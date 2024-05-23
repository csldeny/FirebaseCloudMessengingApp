package com.example.firebasecloudmessengingapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

// Clase donde se crea el servicio que heredara de la clase FirebaseMessagingService
//para poder recibir la notificaion y mostrarla, tambien aqui se crea el token del dispositivo
class MyFireBaseService: FirebaseMessagingService(){
    private val random = Random

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let { message->//Lamba
            Log.w("FCM Title","${message.title}")//muestra en el log el titulo de la notificacion que llego
            Log.i("FCM Body ","${message.body}")//muestra en el log el cuerpo de la notificacion que llego
            sendNotification(message) //se manda llamar la funcion sendNotification
        }
    }

    //aqui se crea el token del dispositivo y se muestra en el log
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Token: $token")
    }

    //sendNotification muestra la notificacion en la barra de notificaciones
    private fun sendNotification(message: RemoteMessage.Notification) {
        /*
        * FLAG_ACTIVITY_CLEAR_TOP asegura que si MainActivity ya está en la pila de actividades, todas las actividades
        * en la parte superior de esta se eliminarán y MainActivity se reutilizará en lugar de crear una nueva instancia.*/
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }

        /*
        PendingIntent puede ser usado más tarde para lanzar MainActivity. La bandera FLAG_IMMUTABLE asegura que el PendingIntent
         no puede ser modificado después de su creación, lo cual es una buena práctica para mejorar la seguridad.
        */

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, FLAG_IMMUTABLE
        )

        val channelId = this.getString(R.string.default_notification_channel_id)

        /*Se declara el diseno de la notficacion la cual se mostrara en el dispositivo*/
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.title)//asiga el titulo
            .setContentText(message.body)//asiga el boody
            .setPriority(NotificationCompat.PRIORITY_HIGH)//La notificación es prominente y alerta al usuario
            .setSmallIcon(R.drawable.icon_google)//asiga una imagen en la notificacion
            .setAutoCancel(true)//Hace que la notificación se descarte automáticamente cuando el usuario la toca.
            .setContentIntent(pendingIntent)//Establece el PendingIntent que se lanzará cuando el usuario toque la notificación.

        // Hace un cast del servicio a NotificationManager, que es la clase responsable de enviar y gestionar las notificaciones.
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        //Comprueba si la versión de Android es al menos Oreo (API nivel 26) porque los canales de notificación son necesarios a partir de esa versión.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        manager.notify(random.nextInt(), notificationBuilder.build())//envia la notificacion
    }

    companion object {
        const val CHANNEL_NAME = "FCM notification channel"
    }
}