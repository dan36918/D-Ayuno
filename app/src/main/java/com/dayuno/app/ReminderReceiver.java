package com.dayuno.app;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
 static final String CHANNEL="dayuno_avisos";
 private static final String[] FAST={
  "Nube: ¡Comienza un nuevo ayuno! Moki: A tu ritmo, estamos contigo.",
  "Moki: Tu ventana de alimentación terminó. Nube: ¡Vamos juntos!",
  "Nube: ¡Un nuevo reto! Moki: Sin prisas, cuídate."
 };
 private static final String[] EAT={
  "Nube: ¡Terminaste tu ayuno! Moki: Buen trabajo, disfruta tu comida.",
  "Moki: Tu objetivo terminó. Nube: ¡Yupi, a comer!",
  "Nube: ¡Lo conseguimos! Moki: Recuerda alimentarte bien."
 };
 static PendingIntent pending(Context c,int type){
  Intent i=new Intent(c,ReminderReceiver.class).putExtra("type",type);
  return PendingIntent.getBroadcast(c,type,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
 }
 static void schedule(Context c,int type,long when){
  AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
  PendingIntent pi=pending(c,type);
  am.cancel(pi);
  if(when<=System.currentTimeMillis())return;
  try {
   if(Build.VERSION.SDK_INT>=31 && !am.canScheduleExactAlarms()) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when,pi);
   else if(Build.VERSION.SDK_INT>=23) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when,pi);
   else am.setExact(AlarmManager.RTC_WAKEUP,when,pi);
  }catch(SecurityException e){am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when,pi);}
 }
 @Override public void onReceive(Context c,Intent intent){
  int type=intent.getIntExtra("type",1);
  NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26){
   NotificationChannel channel=new NotificationChannel(CHANNEL,"Recordatorios D·AYUNO",NotificationManager.IMPORTANCE_DEFAULT);
   channel.setDescription("Mensajes de Nube y Moki al comenzar o terminar el ayuno");
   nm.createNotificationChannel(channel);
  }
  String[] options=type==1?EAT:FAST;
  String msg=options[(int)((System.currentTimeMillis()/86400000L)%options.length)];
  Intent open=new Intent(c,MainActivity.class);
  PendingIntent action=PendingIntent.getActivity(c,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,CHANNEL):new Notification.Builder(c);
  b.setSmallIcon(R.drawable.ic_launcher).setContentTitle(type==1?"🍽️ Hora de comer":"🌙 Hora de empezar el ayuno")
   .setContentText(msg).setStyle(new Notification.BigTextStyle().bigText(msg))
   .setAutoCancel(true).setContentIntent(action);
  try{nm.notify(type,b.build());}catch(SecurityException ignored){}
 }
}
