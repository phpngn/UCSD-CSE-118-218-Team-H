
import {type Writable, writable} from 'svelte/store';

// Initial value of the heart rate store
export const currentHeartRate:Writable<Number> = writable(-1);
export const isFall:Writable<Boolean> = writable(false);
export const isEmergency:Writable<Boolean> = writable(false);

export type Notification = {
    id: number,
    device_id: number,
    title: string,
    checked: boolean,
    timestamp: string,
}
export const notifications:Writable<Notification[]> = writable([]);

const executeSecondsHeartRate = 1;
const executeSecondsIsFall = 1;
const executeSecondsIsEmergency = 1;
const executeSecondsNotifications = 1;

export function startAllSchedulers() {
    startHeartRateScheduler();
    startIsFallScheduler();
    startEmergencyScheduler();
    startNotificationsScheduler();
}

export function startHeartRateScheduler() {
    fetchCurrentHeartRate();
    return setInterval(async () => {
        await fetchCurrentHeartRate();
    }, executeSecondsHeartRate * 1000);
}

export function startIsFallScheduler() {
    fetchCurrentIsFall();
    return setInterval(async () => {
        await fetchCurrentIsFall();
    }, executeSecondsIsFall * 1000);
}

export function startEmergencyScheduler() {
    fetchCurrentIsEmergency();
    return setInterval(async () => {
        await fetchCurrentIsEmergency();
    }, executeSecondsIsEmergency * 1000);
}

export function startNotificationsScheduler() {
    fetchCurrentNotifications();
    return setInterval(async () => {
        await fetchCurrentNotifications();
    }, executeSecondsNotifications * 1000);
}


//const backendHost = 'https://ubiqui-care.fly.dev';
const backendHost = 'http://localhost:8000';

export async function fetchCurrentHeartRate() {
    try {
        const response = await fetch(backendHost+'/api/heartrate');

        if (!response.ok) {
            throw new Error('Failed to fetch data');
        }

        const data = await response.json();
        if (data.message === 'ok') {
            currentHeartRate.set(data.value);
            return;
        }
    } catch (error) {}
    currentHeartRate.set(-1);
}

export async function fetchCurrentIsFall() {
    try {
        const response = await fetch(backendHost+'/api/summary/fall/last');

        if (!response.ok) {
            throw new Error('Failed to fetch data');
        }

        const data = await response.json();
        isFall.set(data.message === 'ok');
        return;
    } catch (error) {}
    isFall.set(false);
}

export async function fetchCurrentIsEmergency() {
    try {
        const response = await fetch(backendHost+'/api/emergency');

        if (!response.ok) {
            throw new Error('Failed to fetch data');
        }

        const data = await response.json();
        isEmergency.set(data.message === 'ok');
        return;
    } catch (error) {}
    isEmergency.set(false);
}

export async function fetchCurrentNotifications() {
    try {
        const response = await fetch(backendHost+'/api/notifications/all');
        if (!response.ok) {
            throw new Error('Failed to fetch data');
        }

        const data = await response.json();
        notifications.set(data);
        return;
    } catch (error) {}
    isEmergency.set(false);
}