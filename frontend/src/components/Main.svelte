<script>
    import {onMount} from "svelte";
    import {apiFetch} from "../api";
    import {RadioTile, TileGroup} from "carbon-components-svelte";
    import Notifications from "./Notifications.svelte";

    let allDataList = [];
    let categorys = [];
    let currentCategory;


    onMount(async () => {
        const res = await apiFetch("/all");
        allDataList = await res.json();
        categorys = allDataList.map(it => it.category);
        currentCategory = categorys[0];
    });

</script>

{#if allDataList.length !== 0}
    <TileGroup on:select={({ detail }) => (currentCategory = detail)}>
        {#each categorys as category}
            <RadioTile {category} checked={currentCategory === category}>{category}</RadioTile>
        {/each}
    </TileGroup>

    <br/>

    Selected: <strong>{currentCategory}</strong>

    <!--    <StructuredList selection>-->
    <!--        <StructuredListHead>-->
    <!--            <StructuredListRow head>-->
    <!--                <StructuredListCell head>url</StructuredListCell>-->
    <!--                <StructuredListCell head>{""}</StructuredListCell>-->
    <!--            </StructuredListRow>-->
    <!--        </StructuredListHead>-->
    <!--        <StructuredListBody>-->
    <!--            {#each allDataList.filter(it => it.category === currentCategory)[0].urls as url}-->
    <!--                <StructuredListRow label for="row-{url}">-->
    <!--                    <StructuredListCell>{url}</StructuredListCell>-->
    <!--                    <StructuredListInput-->
    <!--                            id="row-{url}"-->
    <!--                            value="row-{url}-value"-->
    <!--                            title="row-{url}-title"-->
    <!--                            name="row-{url}-name"-->
    <!--                    />-->
    <!--                    <StructuredListCell>-->
    <!--                        <CheckmarkFilled-->
    <!--                                class="bx&#45;&#45;structured-list-svg"-->
    <!--                                aria-label="select an option"-->
    <!--                                title="select an option"-->
    <!--                        />-->
    <!--                    </StructuredListCell>-->
    <!--                </StructuredListRow>-->
    <!--            {/each}-->
    <!--        </StructuredListBody>-->
    <!--    </StructuredList>-->
{/if}


<!--<Notifications/>-->
