#include "BluetoothSerial.h"
#include "esp_gap_bt_api.h"

#define LED_PIN 13

static void gap_callback(esp_bt_gap_cb_event_t event, esp_bt_gap_cb_param_t *param);

BluetoothSerial SerialBT;
esp_bt_pin_code_t pin_code = {1, 2, 3, 4}; // ここでPINコードを設定

void setup() {
    Serial.begin(115200);

    SerialBT.begin("Bluetooth Classic Test"); // Bluetoothデバイス名を設定
    esp_bt_gap_register_callback(gap_callback);

    // Variable PINタイプを使用して、ユーザーがPINコードを入力できるようにする
    esp_bt_pin_type_t pin_type = ESP_BT_PIN_TYPE_VARIABLE; // 可変のPINコード
    esp_bt_gap_set_pin(pin_type, 0, NULL); // 最初にPINコードを設定しない

    Serial.println("Bluetoothデバイスが起動しました。Androidから接続してください。");

    pinMode(LED_PIN, OUTPUT);
    digitalWrite(LED_PIN, LOW);
}

void loop() {
    static int counter = 0;
    // ここに送信したいデータを記述
    if (SerialBT.connected()) {
        SerialBT.print("Hello from ESP32");
        SerialBT.print(" (");
        SerialBT.print(counter);
        SerialBT.print(")");
        SerialBT.println();
        counter++;
        digitalWrite(LED_PIN, HIGH);
        delay(1000);
    } else {
        digitalWrite(LED_PIN, LOW);
        delay(10);
    }
}

static void gap_callback(esp_bt_gap_cb_event_t event, esp_bt_gap_cb_param_t *param) {
    Serial.print("event: ");
    Serial.println(event);

    switch (event) {
        case ESP_BT_GAP_AUTH_CMPL_EVT:
            Serial.println("認証が完了しました。");
            break;
        case ESP_BT_GAP_PIN_REQ_EVT:
            Serial.println("PINコード認証が要求されました。");
            // PINコードの返信
            esp_bt_gap_pin_reply(param->pin_req.bda, true, 4, pin_code);
            break;

        default:
            break;
    }
}