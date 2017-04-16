
#include <RFduinoBLE.h> 
#include <Wire.h>

#define SIZE 50

#define PERIM_LED 1
#define EMPTY_LED 6
#define PIN_BUZZER 2
#define PIN_SDA 5
#define PIN_SCL 4
#define PIN_INT1 3

#define ACCEL_ADDR 0x1D
#define ACCEL_WRITE 0x3A
#define ACCEL_READ 0x3B
#define CTRL_REG1 0x2A

#define OUT_X_MSB 0x01
#define OUT_Y_MSB 0x03
#define OUT_Z_MSB 0x05

int AccX,AccY,AccZ;
int init_X,init_Y,init_Z;
int status;
int flag_connect = false;
int flag_open = false;



void setup() {

  //On utilise l'affichage série pour débugger
  Serial.begin(9600);
  Serial.println("Serial rate set to 9600 baud");

  //Initialisation des pins
  pinMode(PERIM_LED,OUTPUT); 
  pinMode(EMPTY_LED,OUTPUT);  
  pinMode(PIN_BUZZER,OUTPUT);  

  init_BLE(); 
  //init_Accelero();
}


void loop() 
{
  // switch to lower power mode
  RFduino_ULPDelay(1000);

  //Permet d'envoyer une notification de connexion à l'appli
  if (flag_connect) {
    send_BLE("On",strlen("On"));
  }
  else {
    send_BLE("Off",strlen("Off"));
  }

  //lecture_Accelero();

  //Envoi d'un avertissement d'ouverture du frigo à l'appli
  if (ouverture(AccX,AccY,AccZ)) {
    send_BLE("Open",strlen("Open"));
    Buzzer();
  }
  else {
    send_BLE("Close",strlen("Close"));
  }

}



void init_Accelero()
{
  //Initialisation des pins pour l'i2C et lancement d'une transmission
  Wire.beginOnPins(PIN_SCL,PIN_SDA);
  delay(20);

  //Ecriture dans un registre de config pour activer 
  //F_READ et ACTIVE MODE
  Wire.beginTransmission(ACCEL_WRITE);
  Wire.write(CTRL_REG1);
  Wire.write(0x03);
  status = Wire.endTransmission();
  if (status == 0) { Serial.println("Configuration successful"); }
}



//Récupère les valeurs pour chaque axe de l'accéléromètre
void lecture_Accelero()
{
  //On choisit le registre sur lequel la lecture commence
  Wire.beginTransmission(ACCEL_WRITE);
  Wire.write(OUT_X_MSB);
  status = Wire.endTransmission(false);
  if (status == 0) { Serial.println("Mode lecture"); }

  //On peut maintenant lire les 3 registres X,Y et Z
  Wire.requestFrom(ACCEL_READ,3);

  //Tant que le bus i2C n'est pas vide
  if (Wire.available() <= 3) {
    Serial.print("bonjour");
    AccX = Wire.read();
    AccY = Wire.read();
    AccZ = Wire.read();
  }

  //Affichage des coordonnées
  Serial.print("X = ");
  Serial.print(AccX);
  Serial.print(" Y = ");
  Serial.print(AccY);
  Serial.print(" Z = ");
  Serial.println(AccZ);

  delay(500); 
}


//Retourne true si la porte du frigo est ouverte, 0 sinon
boolean ouverture(int AccX,int AccY,int AccZ)
{
  //Si les coordonnées changent, la porte a été ouverte
  if (init_X != AccX or init_Y != AccY or init_Z != AccZ) {
    flag_open = true;
  }

  //Si les coordonnées sont identiques à celles enregistrées
  //juste avant l'ouverture de la porte, elle s'est fermée
  if (init_X == AccX and init_Y == AccY and init_Z == AccZ) {
    flag_open = false;
  }


  //On récupère toujours les valeurs précédentes lorsque la porte n'est 
  // pas ouverte pour pouvoir les comparer aux suivantes
  if (!flag_open) {
    init_X = AccX;
    init_Y = AccY;
    init_Z = AccZ;
  }

  return flag_open;
}


//Active le buzzer
void Buzzer()
{
  digitalWrite(PIN_BUZZER,HIGH);
  delay(3);
  digitalWrite(PIN_BUZZER,LOW);
  delay(3);
}



void init_BLE()
{
  //Initialisation de la RFDuino
  RFduinoBLE.deviceName = "RFOptali";
  Serial.println("Modification du deviceName");
  
  //Démarrage du BLE
  RFduinoBLE.begin();
  Serial.println("BLE en marche");
  delay(20);
  
}

//Envoi de données
void send_BLE(const char* data,int len)
{
  RFduinoBLE.send(data,len);
}


//Callback sur connexion bluetooth
void RFduinoBLE_onConnect()
{
  flag_connect = true;
  Serial.println("Connexion");
}


//Callback sur déconnexion bluetooth
void RFduinoBLE_onDisconnect()
{
  flag_connect = false;
  Serial.println("Deconnexion");
}


//Actions à réaliser lors de la réception de données
void RFduinoBLE_onReceive(char* data,int len)
{
  if (strstr(data,"coucou") != NULL)
  {  
    Serial.println("Récupération des données effectuée");
    Serial.println("Envoi de la réponse");
  }

  if (strstr(data,"Perim") != NULL) {
    digitalWrite(PERIM_LED,HIGH);
  }

  if (strstr(data,"NotPerim") != NULL) {
    digitalWrite(PERIM_LED,LOW);
  }

  if (strstr(data,"Vide") != NULL) {
    digitalWrite(EMPTY_LED,HIGH);
  }

  if (strstr(data,"Plein") != NULL) {
    digitalWrite(EMPTY_LED,LOW);
  }  
}

