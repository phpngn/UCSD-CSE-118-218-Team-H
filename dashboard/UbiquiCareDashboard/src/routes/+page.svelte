<svelte:head>
	<title>Home</title>
	<meta name="description" content="Svelte demo app" />
</svelte:head>
<script>
	import { onMount } from 'svelte';
	import {currentHeartRate, isEmergency, isFall, notifications, startAllSchedulers} from "$lib/data";
	onMount(startAllSchedulers);

	let mockNotify = [
		{
			id:1,
			device_id:1,
			title: 'Heart rate is too high',
			timestamp: '2021-05-01 12:00:00'
		},
		{
			id:2,
			device_id:1,
			title: 'Heart rate is too high',
			timestamp: '2021-05-01 12:00:00'
		},
		{
			id:3,
			device_id:1,
			title: 'Heart rate is too high',
			timestamp: '2021-05-01 12:00:00'
		}
	]
</script>
<style>

	* {
		font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
		font-size: 14px;
		font-weight: 400;
		color: #3b3b3b;
	}

	h1 {
		display: block;
		position: relative;
		width: 100%;
		height: 50px;
		font-weight: 600;
		font-size: 25px;
		margin-left: 30px;
		margin-top: 50px;
		margin-bottom: 0;;
	}

	.container {
		display: block;
		position: relative;
		width: 100%;
		height: auto;
		padding: 20px;
	}

	.widget {
		display: inline-block;
		position: relative;
		vertical-align: top;
		width: 300px;
		height: 250px;
		border-radius: 6px;
		box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
		margin: 0 10px
	}

	#heartrate .value {
		display: block;
		position: absolute;
		width: 100%;
		font-size: 65px;
		font-weight: 500;
		text-align: center;
		top:50%;
		transform: translateY(-50%);
		color: #ffffff;
		margin-top: -15px;
	}

	#heartrate .label {
		display: block;
		position: absolute;
		width: 100%;
		font-size: 16px;
		font-weight: 500;
		text-align: center;
		top:50%;
		transform: translateY(-50%);
		margin-top: 35px;
		color: #ffffff;
	}
	#falls img {
		display: block;
		position: absolute;
		left: 50%;
		top: 50%;
		transform: translate(-50%, -50%);
		width: 80px;
		height: 80px;
	}
	#emergency img {
		display: block;
		position: absolute;
		left: 50%;
		top: 50%;
		transform: translate(-50%, -50%);
		width: 60px;
		height: 60px;
		margin-top: -15px;
	}
	#emergency .label {
		display: block;
		position: absolute;
		width: 100%;
		font-size: 16px;
		font-weight: 500;
		text-align: center;
		top:50%;
		transform: translateY(-50%);
		margin-top: 35px;
		color: #ffffff;
	}
	#notifications {
		width: 600px;
		min-height: 244px;
		height: auto;
		max-height: 487px;
		background: #fcfcfc;
		overflow-x: hidden;
		overflow-y: auto;
	}
	.notification {
		display: block;
		position: relative;
		width: 100%;
		height: 60px;
		border-bottom: 1px solid rgba(0,0,0,0.1);
	}
	.notification img {
		display: inline-block;
		position: relative;
		vertical-align: middle;
		width: 30px;
		height: 30px;
		margin-left: 20px;
		margin-right: 25px;
	}
	.notification .title {
		display: inline-block;
		position: relative;
		vertical-align: middle;
		font-size: 16px;
		font-weight: 500;
		color: #3b3b3b;
		line-height: 60px;
		max-width: 360px;
		overflow: hidden;
		white-space: nowrap;
	}
	.notification .timestamp {
		display: inline-block;
		position: absolute;
		right: 20px;
		top: 50%;
		transform: translateY(-50%);
		font-size: 14px;
		font-weight: 400;
		color: #afafaf;
		line-height: 60px;
	}
</style>
<h1>Dashboard</h1>
<div class="container">
	<div class="widget" id="heartrate" style="background: {$currentHeartRate > 50 ? ($currentHeartRate > 100 ? $currentHeartRate > 200 ? '#ff0000' : '#ff7200' : '#4db724') : '#ff0000'}">
		<span class="value">{$currentHeartRate !== -1 ? $currentHeartRate : '?'}</span>
		<span class="label">Heart rate</span>
	</div>
	<div class="widget" id="falls" style="background: {$isFall ? '#ff0000' : '#4db724'}">
		<img src="{$isFall ? '/falling.png' : '/standing.png'}" alt="" class="value">
	</div>
	<div class="widget" id="emergency" style="background: {$isEmergency ? '#ff0000' : '#4db724'}">
		<img src="{$isEmergency ? '/sos.png' : '/ok.png'}" alt="" class="value">
		<span class="label">Emergency</span>
	</div>
	<div class="widget" id="notifications">
		{#each mockNotify as notification}
			<div class="notification">
				<img src="/notification.png" alt="" class="icon">
				<span class="title">{notification.title}</span>
				<span class="timestamp">{notification.timestamp}</span>
			</div>
		{/each}
	</div>
</div>