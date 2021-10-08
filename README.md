# DHBWTalkServer
 
API für DHBW Talk Clients zur Abfrage gegen eine Datenbank (Standard Xampp)

Abfragen und Antworten der API

localhost ist der Platzhalter. Für den Fall, dass ein Server aufgesetzt wird muss die IP offensichtlich angepasst werden.
Der Standard-Port ist 8080. Die Kommunikation findet per HTTP statt,

Um einen neuen Studenten(User) zu registrieren:
Post: http://localhost:8080/students/ Body(Student) -> 
{"name":"Testname" , "surname":"TestNachname" , "gender":"Testgender" , "courseID":"TestKurs"} [Body muss komplett befüllt sein]
Mögliche Antworten: "Student angelegt" oder "Student abgewiesen"

Um potentielle Freunde anzufragen:
Get: http://localhost:8080/students/ Body(FreundesAnfrage(abzufrageneder Student)) -> 
{"name":"null" , "surname":"null" , "courseID":"TestKurs"} [Body muss nicht komplett befüllt sein, aber mindestens ein Eintrag muss vorhanden sein]
Beispiel Antwort: [{"name":"Testname" , "surname":"Testnachname" , "courseID":"TestKurs"},]

Um eine Freundschaftsanfrage abzusenden:
Post: http://localhost:8080/students/{matrikelnummer}/freunde Body(FreundesAnfrage(zu befreundender Student)) ->
{"name":"Testname" , "surname":"TestNachname" , "courseID":"TestKurs"} [Body muss komplett befüllt sein die hierfür notwendigen Informationen sind aus der Antwort der "potentiellen Freunde anfrage" zu entnehmen]
Mögliche Antworten: "Freundesanfrage gesendet" oder "Freundschaftsanfrage abgelehnt"

Um offene Freundschaftsanfragen abzufragen:
Get: http://localhost:8080/students/{matrikelnummer}/offenefreunde kein Body
Mögliche Antworten: [{"name":"Testname" , "surname":"Testnachname" , "courseID":"Testkurs"},]

Um eine Freundschaft zu bestätigen:
Put: http://localhost:8080/students/{matrikelnummer}/freunde Body(FreundesAnfrage(des zu bestätigenden Studenten)) ->
{"name":"Testname" , "surname":"Testnachname" , "courseID":"Testkurs"} [Body muss komplett befüllt sein, die hierfür notwendigen Informationen sind aus der offenen Freundschaftsanfrage zu entnehmen]
Mögliche Antworten: "Freundschaft bestaetigt" oder "Freundschaft abegelehnt"

Um bestehende Freunde abzufragen:
Get: http://localhost:8080/students/{matrikelnummer}/freunde kein Body
Mögliche Antworten: "keine Freunde" oder [{"name":"Testname" , "surname":"TestNachname" , "courseID";"Testkurs"},]
