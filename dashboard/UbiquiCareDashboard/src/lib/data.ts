
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
        await fetchCurrentHeartRate();
    }, executeSecondsHeartRate * 1000);
}

export function startIsFallScheduler() {
    fetchCurrentIsFall();
    return setInterval(async () => {
        await fetchCurrentIsFall();
    }, executeSecondsIsFall * 1000);
}


const backendHost = 'https://ubiqui-care.fly.dev/';
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