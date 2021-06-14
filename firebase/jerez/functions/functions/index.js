const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.onCreateNews = functions.firestore.document('news' + '/{documentId}')
	.onCreate((snap, context) => {

    const data = snap.data();
    const documentId = context.params.documentId;
    console.log(`onCreate news: ${documentId} ${data.title}`);

    return sendNotification('Nueva noticia', data.title, data.image, documentId, 'general');

});

exports.onCreateNewsTest = functions.firestore.document('news_test' + '/{documentId}')
	.onCreate((snap, context) => {

    const data = snap.data();
    const documentId = context.params.documentId;
    console.log(`onCreate news_test: ${documentId} ${data.title}`);

    return sendNotification('Nueva noticia - Test', data.title, data.image, documentId, 'general_test');

});


function sendNotification(title, body, image, documentId, topic) {

    console.log(`Sending notif: ${title}`);
    console.log(`TOPIC ${topic}`);

    const message = {
        notification:{
            title: title,
            body: body
        },
        data: {
            id: documentId
        },
        android: {
            notification: {
              imageUrl: image
            }
        },
        topic: topic
    };

    return admin.messaging().send(message)
        .then(response => {
            console.log("Notifications sent");
            return response;
        })
        .catch(err => {
            console.log('Error sending notification ', err);
        });


}