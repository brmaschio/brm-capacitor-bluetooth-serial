import { BrMCapacitorBluetoothSerial } from 'brm-capacitor-bluetooth-serial';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    BrMCapacitorBluetoothSerial.echo({ value: inputValue })
}
