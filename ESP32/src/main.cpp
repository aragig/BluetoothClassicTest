#include "BluetoothSerial.h"
#include "esp_gap_bt_api.h"

#define LED_PIN 13

/**
 * Bluetoothデバイスのコールバック関数
 *
 * @param event
 * @param param
 */
static void gap_callback(esp_bt_gap_cb_event_t event, esp_bt_gap_cb_param_t *param);

BluetoothSerial SerialBT;
esp_bt_pin_code_t pin_code = {1, 2, 3, 4}; // ここでPINコードを設定

/**
 * Bluetoothデバイスの初期化
 *
 * 1. BluetoothSerial.begin()でBluetoothデバイスを初期化
 * 2. esp_bt_gap_register_callback()でコールバック関数を登録
 * 3. esp_bt_pin_type_tでPINコードのタイプを設定
 * 4. esp_bt_gap_set_pin()でPINコードを設定
 *
 * @return void
 */
void setup() {
    Serial.begin(115200);

    SerialBT.begin("BTC Device 01"); // Bluetoothデバイス名を設定
    esp_bt_gap_register_callback(gap_callback);

    // Variable PINタイプを使用して、ユーザーがPINコードを入力できるようにする
    esp_bt_pin_type_t pin_type = ESP_BT_PIN_TYPE_VARIABLE; // 可変のPINコード
    esp_bt_gap_set_pin(pin_type, 0, NULL); // 最初にPINコードを設定しない

    Serial.println("Bluetoothデバイスが起動しました。Androidから接続してください。");

    pinMode(LED_PIN, OUTPUT);
    digitalWrite(LED_PIN, LOW);
}

/**
 * Bluetoothデバイスのループ処理
 *
 * 1. BluetoothSerial.connected()で接続状態を確認
 * 2. 接続中の場合は、BluetoothSerial.print()でデータを送信
 * 3. 送信したら、LEDを点灯させて1秒待機
 * 4. 未接続の場合は、LEDを消灯させて10ミリ秒待機
 *
 * @return void
 */
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