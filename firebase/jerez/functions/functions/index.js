const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.onCreateNews = functions.firestore.document('news' + '/{document}')
	.onCreate((snap, context) => {

    const data = snap.data();

    console.log(`onCreate news${ENV_SUFFIX}: ${data.title}`);

    return sendNotification('Nueva noticia', data.title, 'general');

});

exports.onCreateNewsTest = functions.firestore.document('news_test' + '/{document}')
	.onCreate((snap, context) => {

    const data = snap.data();

    console.log(`onCreate news${ENV_SUFFIX}: ${data.title}`);

    return sendNotification('Nueva noticia - Test', data.title, 'general_test');

});


function sendNotification(title, body, topic) {

  console.log(`Sending notif: ${title}`);
  console.log(`TOPIC ${topic}`);


    const payLoad = {
        notification:{
            title: title,
            body: body,
            sound: "default"
        }
    };

    const options = {
        priority: "high",
        timeToLive: 60*60*24
    };

    return admin.messaging().sendToTopic(topic, payLoad, options)
        .then(response => {
                    console.log("Notifications sent");
                    return response;
                })
                .catch(err => {
                    console.log('Error sending notification ', err);
                });


}