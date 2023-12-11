import {onMount} from "svelte";
import {currentHeartRate, isFall, startAllSchedulers} from "$lib/data";
import {get} from "svelte/store";

export const prerender = true;

export async function load({ params }) {

}