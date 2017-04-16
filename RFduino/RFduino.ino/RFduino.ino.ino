
#include <RFduinoBLE.h> 
#include <Wire.h>

#define SIZE 50

#define PIN_LED2 1
#define PIN_LED3 6
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
int status;
int flag = false;



void setup() {

  //On utilise l'affichage série pour débugger
  Serial.begin(9600);
  Serial.println("Serial rate set to 9600 baud");

  //Initialisation des pins
  pinMode(PIN_LED2,OUTPUT); 
  pinMode(PIN_LED3,OUTPUT); 
  pinMode(PIN_INT1,INPUT);  
  pinMode(PIN_BUZZER,OUTPUT);  

  init_BLE(); 
  //init_Accelero();
}


void loop() 
{
  // switch to lower power mode
  RFduino_ULPDelay(INFINITE);
  
  //Buzzer();
  //lecture_Accelero();
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



void lecture_Accelero()
{
  //On choisit le registre sur lequel la lecture commence
  Wire.beginTransmission(ACCEL_WRITE);
  Wire.write(OUT_X_MSB);
  status = Wire.endTransmission(false);
  if (status == 4) { Serial.println("Mode lecture"); }

  //On peut maintenant lire les 3 registres X,Y et Z
  Wire.requestFrom(ACCEL_READ,3);
  
  if (Wire.available() <= 3) {
    Serial.print("bonjour");
    AccX = Wire.read();
    AccY = Wire.read();
    AccZ = Wire.read();
  }
  
  Serial.print("X = ");
  Serial.print(AccX);
  Serial.print(" Y = ");
  Serial.print(AccY);
  Serial.print(" Z = ");
  Serial.println(AccZ);

  delay(5000); 
}



void Buzzer()
{
  digitalWrite(PIN_BUZZER,HIGH);
  Serial.println(digitalRead(PIN_BUZZER));
  delay(3);
  digitalWrite(PIN_BUZZER,LOW);
  Serial.println(digitalRead(PIN_BUZZER));
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

//envoi de donnees
void send_BLE(const char* data,int len)
{
  RFduinoBLE.send(data,len);
}



void RFduinoBLE_onConnect()
{
  flag = true;
  send_BLE("On",strlen("On"));
  delay(20);
  Serial.println("Connexion");
}


void RFduinoBLE_onDisconnect()
{
  flag = false;
  send_BLE("Off",strlen("Off"));
  delay(20);
  Serial.println("Deconnexion");
}



void RFduinoBLE_onReceive(char* data,int len)
{
  if (strstr(data,"coucou") != NULL)
  {  
    Serial.println("Récupération des données effectuée");
    Serial.println("Envoi de la réponse");

    send_BLE("Open",strlen("Open"));
  }
}



void envoi_Donnees()
{
  if (flag)
  {
    //Envoi des coordonnees de l'accelerometre
    //avec fonction send_BLE
  }
}

