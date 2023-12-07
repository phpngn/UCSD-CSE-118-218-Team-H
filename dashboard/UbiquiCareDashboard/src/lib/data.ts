
import {type Writable, writable} from 'svelte/store';

// Initial value of the heart rate store
export const currentHeartRate:Writable<Number> = writable(-1);
export const isFall:Writable<Boolean> = writable(false);

const executeSecondsHeartRate = 5;
const executeSecondsIsFall = 5;

export function startAllSchedulers() {
    startHeartRateScheduler();
    startIsFallScheduler();
}

export function startHeartRateScheduler() {
    fetchCurrentHeartRate();
    return setInterval(async () => {
        console.log("Fetching Heartrate...");
        await fetchCurrentHeartRate();
    }, executeSecondsHeartRate * 1000);
}

export function startIsFallScheduler() {
    fetchCurrentIsFall();
    return setInterval(async () => {
        console.log("Fetching fall status...");
        await fetchCurrentIsFall();
    }, executeSecondsIsFall * 1000);
}


const backendHost = 'http://localhost:8000';
export async function fetchCurrentHeartRate() {
    try {
        const response = await fetch(backendHost+'/api/heartrate');

        if (!response.ok) {
            throw new Error('Failed to fetch data');
        }

        const data = await response.json();
        if (data.message === 'ok') {
            currentHeartRate.set(data.last);
            return;
        }
    } catch (error) {}
    currentHeartRate.set(-1);
}

export async function fetchCurrentIsFall() {
    isFall.set(false);
}