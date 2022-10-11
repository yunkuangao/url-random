<script>
    import { ToastNotification } from 'carbon-components-svelte';
    import { notificationStore } from '../store/notification';
</script>

<div class="notification-position">
    {#each $notificationStore as n}
        <ToastNotification
            kind={n.level}
            title=""
            caption=""
            subtitle={n.content}
            timeout="4000"
        />
    {/each}
</div>

<style>
    .notification-position {
        position: fixed;
        width: 400px;
        z-index: 99999;
    }
</style>
