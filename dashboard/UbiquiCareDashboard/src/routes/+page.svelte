<svelte:head>
	<title>Home</title>
	<meta name="description" content="Svelte demo app" />
</svelte:head>
<script>
	import { onMount } from 'svelte';
	import {currentHeartRate, isFall, startAllSchedulers} from "$lib/data";
	onMount(startAllSchedulers);
</script>
<style>

</style>
<div class="container">
	<h1>UbiquiCare Dashboard</h1>
	<span class="fall">Is Fall: {$isFall}</span>
	<span class="heartrate">Current heart rate: {$currentHeartRate}</span>
</div>