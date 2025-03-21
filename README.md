# brm-capacitor-bluetooth-serial

Plugin to work with ionic and capacitor using bluetooth

The difference in this project is the possibility of communicating both in **TEXT** and in **HEXADECIMAL**.

Supported platforms

- [ ] Web
- [x] Android
- [ ] iOS

## Install

```bash
npm i @brmaschio/brm-capacitor-bluetooth-serial
npx cap sync
```

## API

* [`hasPermitions()`](#haspermitions)
* [`requestPermissions()`](#requestpermissions)
* [`isEnabled()`](#isenabled)
* [`listPairedDevices()`](#listpaireddevices)


* [`connect(...)`](#connect)
* [`disconnect(...)`](#disconnect)
* [`isConnected(...)`](#isconnected)
* [`write(...)`](#write)
* [`read(...)`](#read)
* [Enums](#enums)

--------------------

### hasPermitions()

Check if bluetooth permissions are granted.

```typescript
hasPermitions() => Promise<{ hasPermitions: boolean; }>
```

**Returns:** <code>Promise&lt;{ hasPermitions: boolean; }&gt;</code>


```typescript
import { BrMCapacitorBluetoothSerial } from '@brmaschio/brm-capacitor-bluetooth-serial';

BrMCapacitorBluetoothSerial.hasPermitions().then(response => {
     console.log('hasPermitions', response.hasPermitions);
}).catch(() => {
    console.log('Error');
});
```

--------------------

### requestPermissions()

Ask the device for permissions to use Bluetooth

```typescript
requestPermissions() => Promise<{ hasPermitions: boolean; }>
```

**Returns:** <code>Promise&lt;{ hasPermitions: boolean; }&gt;</code>

```typescript
import { BrMCapacitorBluetoothSerial } from '@brmaschio/brm-capacitor-bluetooth-serial';

BrMCapacitorBluetoothSerial.requestPermissions().then(response => {
    console.log('hasPermitions', response.hasPermitions);
}).catch(() => {
    console.log('Error');
});
```

--------------------

### isEnabled()

Checks if Bluetooth is activated, considering three criteria, if the device has the feature, if it has the permissions granted, and if it is turned on.

```typescript
isEnabled() => Promise<{ isEnabled: boolean; }>
```

**Returns:** <code>Promise&lt;{ isEnabled: boolean; }&gt;</code>

```typescript
import { BrMCapacitorBluetoothSerial } from '@brmaschio/brm-capacitor-bluetooth-serial';

BrMCapacitorBluetoothSerial.isEnabled().then(response => {
    console.log('isEnabled', response.isEnabled);
}).catch(() => {
    console.log('Error');
});
```

--------------------

### listPairedDevices()

Paired Devices List.

```typescript
listPairedDevices() => Promise<{ devices: Device[]; }>
```

**Returns:** <code>Promise&lt;{ devices: Device[]; }&gt;</code>

```typescript
import { BrMCapacitorBluetoothSerial } from '@brmaschio/brm-capacitor-bluetooth-serial';

BrMCapacitorBluetoothSerial.listPairedDevices().then(response => {
    console.log('devices', response.devices);
}).catch(() => {
    console.log('Error');
});
```

--------------------











### connect(...)

Connect to a specific device, and inform whether the communication will work as text or hexadecimal.

```typescript
connect(options: { address: string; mode: EditorMode; }) => Promise<{ connected: boolean; }>
```

| Param         | Type                                                                          |
| ------------- | ----------------------------------------------------------------------------- |
| **`options`** | <code>{ address: string; mode: <a href="#editormode">EditorMode</a>; }</code> |

**Returns:** <code>Promise&lt;{ connected: boolean; }&gt;</code>

```typescript
import { BrMCapacitorBluetoothSerial, EditorMode } from '@brmaschio/brm-capacitor-bluetooth-serial';

BrMCapacitorBluetoothSerial.connect({ address: '00:11:22:33:44:55', mode: EditorMode.TEXT }).then(response => {
    console.log('connected', response.connected);
}).catch(() => {
    console.log('Error');
});
```

--------------------


### disconnect(...)

Disconnect with a specific device.

```typescript
disconnect(options: { address: string; }) => Promise<{ disconnected: boolean; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;{ disconnected: boolean; }&gt;</code>


```typescript
import { BrMCapacitorBluetoothSerial } from '@brmaschio/brm-capacitor-bluetooth-serial';

BrMCapacitorBluetoothSerial.disconnect({ address: '00:11:22:33:44:55' }).then(response => {
    console.log('disconnected', response.disconnected);
}).catch(() => {
    console.log('Error');
});
```

--------------------

### isConnected(...)

Check if you are connected to a specific device.

```typescript
isConnected(options: { address: string; }) => Promise<{ isConnected: boolean; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;{ isConnected: boolean; }&gt;</code>

```typescript
import { BrMCapacitorBluetoothSerial } from '@brmaschio/brm-capacitor-bluetooth-serial';

BrMCapacitorBluetoothSerial.isConnected({ address: '00:11:22:33:44:55' }).then(response => {
    console.log('isConnected', response.isConnected);
}).catch(() => {
    console.log('Error');
});
```

--------------------


### write(...)

Send a command to the connected device.

```typescript
write(options: { address: string; command: string; }) => Promise<void>
```

| Param         | Type                                               |
| ------------- | -------------------------------------------------- |
| **`options`** | <code>{ address: string; command: string; }</code> |

```typescript
import { BrMCapacitorBluetoothSerial } from '@brmaschio/brm-capacitor-bluetooth-serial';

BrMCapacitorBluetoothSerial.write({ address: '00:11:22:33:44:55', command: 'Hello' }).then(() => {
    console.log('sucess');
}).catch(() => {
    console.log('Error');
});
```

--------------------

### read(...)

```typescript
read(options: { address: string; }) => Promise<{ data: boolean; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;{ data: boolean; }&gt;</code>

```typescript
import { BrMCapacitorBluetoothSerial } from '@brmaschio/brm-capacitor-bluetooth-serial';

BrMCapacitorBluetoothSerial.read({ address: '00:11:22:33:44:55' }).then(response => {
    console.log('response data', response.data);
}).catch(() => {
    console.log('Error');
});
```

--------------------

### Enums

#### EditorMode

| Members    | Value               |
| ---------- | ------------------- |
| **`TEXT`** | <code>"TEXT"</code> |
| **`HEX`**  | <code>"HEX"</code>  |

