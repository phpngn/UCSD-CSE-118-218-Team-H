<script>
  import {
    Navbar,
    NavBrand,
    NavLi,
    NavUl,
    NavHamburger,
  } from "flowbite-svelte";
  import "../app.pcss";

</script>

<div class="container h-screen w-screen mx-auto">
  <Navbar class="text-center">
    <NavBrand href="/">
      <span
        class="self-center whitespace-nowrap text-xl font-semibold dark:text-white"
        >Ubiqui.Care</span
      >
    </NavBrand>
    <NavHamburger />
    <NavUl>
      <NavLi href="/">Home</NavLi>
      <NavLi href="/docs/components/navbar">Navbar</NavLi>
      <NavLi href="/pricing">Pricing</NavLi>
    </NavUl>
  </Navbar>
  <slot />
</div>
