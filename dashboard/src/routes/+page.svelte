<script>
    import {Card} from "flowbite-svelte";
    import { Heading, P, A, Mark, Secondary } from 'flowbite-svelte';
    let heartrate = 0;

    setInterval(() => {
        heartrate = Math.floor(Math.random() * 100);
    }, 1000);
</script>

<div class="text-center">
  <Heading tag="h1" class="mb-4" customSize="text-4xl font-extrabold  md:text-5xl lg:text-6xl">Ququi Care</Heading>
  <P class="mb-6 text-lg lg:text-xl sm:px-16 xl:px-48 dark:text-gray-400"></P>
</div>

<Card class="text-5xl font-bold text-center py-15">
    {heartrate}
</Card>
