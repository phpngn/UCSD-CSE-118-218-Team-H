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
	body {
		display: block;
		position: relative;
		width: 100%;
		height: 100%;
		margin: 0;
		padding: 0;
		overflow: hidden !important;
	}

	.hr span, .fall span {
		display: block;
		position: relative;
		width: 100%;
		line-height: 100vh;
		text-align: center;
		font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
		font-size: 50px;
		font-weight: 600;
	}

	.hr {
		display: block;
		position: absolute;
		width: 50%;
		height: 100%;
		left:0;
	}

	.fall {
		display: block;
		position: absolute;
		width: 50%;
		height: 100%;
		right:0;
	}
	.fallen {
		background: #ff0000;
		color: #ffffff;
	}

	.standing {
		background: #80ea2e;
		color: #ffffff;
	}

	.unknown {
		background: #c2c2c2;
		color: #3b3b3b;
	}
	.loaded {
		background: #ffae00;
		color: #ffffff;
	}
</style>
<div class="container">
	<div class="hr {$currentHeartRate == -1 ? 'unknown' : 'loaded'}">
		<span class="heartrate">Current heart rate: {$currentHeartRate != -1 ? $currentHeartRate : 'Unavailable'}</span>
	</div>
	<div class="fall {$isFall ? 'fallen' : 'standing'}">
		<span class="fall">{$isFall ? 'Fallen' : 'Standing'}</span>
	</div>
</div>