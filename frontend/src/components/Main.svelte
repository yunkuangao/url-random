<script>
    import { onMount } from 'svelte';
    import { apiFetch } from '../api';
    import {
        Modal,
        Button,
        TextInput,
        StructuredList,
        StructuredListRow,
        StructuredListHead,
        StructuredListCell,
        StructuredListBody,
        StructuredListInput
    } from 'carbon-components-svelte';
    import CheckmarkFilled from 'carbon-icons-svelte/lib/CheckmarkFilled16';
    import Tips from './Notifications.svelte';
    import { notify } from '../store/notification';

    let allDataList = []; // 全量数据

    let categories = []; // 当前显示的分类，用于搜索
    let currentCategory = ''; // 当前所选分类，获取默认使用第一个
    let categorySearch = ''; // 分类搜索
    let categoryModal = false; // 分类添加弹窗控制
    let addCategoryValue = ''; // 新增分类
    let categoryInvalidText = ''; // 分类无效信息
    $: categoryInvalidText = handleCategoryValida(addCategoryValue); // 监听分类信息修改校验信息

    let urls = []; // 当前分类的url集合
    let urlSearch = ''; // url搜索
    let addUrlValue = ''; // 新增url
    let urlModal = false;
    let urlInvalidText = ''; // url无效信息
    $: urlInvalidText = handleUrlValida(addUrlValue); // 监听分类信息修改校验信息

    let first = false;
    onMount(async () => {
        const res = await apiFetch('/all');
        allDataList = await res.json();
        currentCategory = (allDataList[0] || {}).category;
        categories = allDataList.map((item) => item.category);
        urls = (allDataList[0] || {}).urls;
        if (currentCategory) {
            first = true;
            setTimeout(() => {
                clickCategory();
            });
        }
    });

    // 防抖
    function debounce(fn, time = 100) {
        let timer = 0;
        return function () {
            const args = Array.prototype.slice.call(arguments);
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(() => {
                fn.call(fn, ...args);
            }, time);
        };
    }

    // 手动模拟分类点击
    function clickCategory() {
        const ele = document.querySelector('#row-' + currentCategory);
        ele && ele.click();
    }

    /**
     * category start-----------------
     */

    // 分类搜索
    const handleCategorySearch = debounce(async () => {
        categories = allDataList
            .filter((item) => item.category.includes(categorySearch))
            .map((item) => item.category);
        if (!categories.includes(currentCategory)) {
            if (categories.length) {
                currentCategory = categories[0];
                clickCategory();
            } else {
                currentCategory = '';
            }
        }
    }, 200);

    // 处理分类删除
    const handleCategoryDelete = async (item) => {
        const res = await apiFetch('/category/delete', {
            method: 'POST',
            body: JSON.stringify({
                category: item
            })
        });
        if (res.status === 200) {
            notify(`【${item}】分类删除成功`, 'success');
            allDataList = allDataList.filter((l) => l.category !== item);
            categories = allDataList.map((item) => item.category);
            handleCategorySearch();
            if (currentCategory === item) {
                if (categories.length) {
                    currentCategory = categories[0];
                    clickCategory();
                } else {
                    currentCategory = '';
                    urls = [];
                }
            }
        } else {
            notify('删除失败，' + res.statusText, 'error');
        }
    };

    // 处理分类验证
    const handleCategoryValida = (value) => {
        if (!value) return '不可为空';
        if (categories.includes(value)) return '分类重复';
        return '';
    };

    // 处理分类点击
    const handleCategoryChange = async () => {
        if (first) {
            first = false;
            return;
        }
        handleUrlSearch();
    };

    // 处理分类添加
    const handleCategorySubmit = async () => {
        if (handleCategoryValida(addCategoryValue)) return;
        const res = await apiFetch('/category/add', {
            method: 'POST',
            body: JSON.stringify({
                category: addCategoryValue
            })
        });
        if (res.status === 200) {
            notify(`【${addCategoryValue}】分类添加成功`, 'success');
            allDataList.push({
                category: addCategoryValue,
                urls: []
            });
            handleCategorySearch();
            addCategoryValue = '';
        } else {
            notify('添加失败，' + res.statusText, 'error');
        }
        document.querySelector('#category').focus();
    };

    const BASIC_PATH = 'https://localhost:8080/random/';
    // 处理分类复制
    const handleCategoryCopy = (item) => {
        navigator.clipboard.writeText(BASIC_PATH + item);
        notify('复制成功', 'success');
    };

    /**
     * category end-----------------
     */

    /**
     * url start--------------------
     */

    // 处理url搜索
    const handleUrlSearch = debounce(() => {
        const urlArr =
            (
                allDataList.find((item) => item.category === currentCategory) ||
                {}
            ).urls || [];
        urls = urlArr.filter((item) => item.includes(urlSearch));
    }, 200);

    const URL_REGEXP =
        /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()!@:%_\+.~#?&\/\/=]*)/;
    // 处理URL验证
    const handleUrlValida = (value) => {
        if (!URL_REGEXP.test(value)) return '格式不正确';
        if (urls && urls.includes(value)) return 'url重复';
        return '';
    };

    // 处理url删除
    const handleUrlDelete = async (item) => {
        const res = await apiFetch('/url/delete', {
            method: 'POST',
            body: JSON.stringify({
                category: currentCategory,
                url: item
            })
        });
        if (res.status === 200) {
            notify(`【${item}】删除成功`, 'success');

            let urlArr =
                (allDataList.find((l) => l.category === currentCategory) || {})
                    .urls || [];
            if (urlArr.length) {
                const index = urlArr.findIndex((url) => url === item);
                index > -1 && urlArr.splice(index, 1);
                urls = urlArr;
            }
        } else {
            notify('删除失败，' + res.statusText, 'error');
        }
    };

    // 处理分类添加
    const handleUrlSubmit = async () => {
        const res = await apiFetch('/url/add', {
            method: 'POST',
            body: JSON.stringify({
                category: currentCategory,
                url: addUrlValue
            })
        });
        if (res.status === 200) {
            notify(`【${addUrlValue}】添加成功`, 'success');
            let urlArr = allDataList.find(
                (l) => l.category === currentCategory
            ).urls;
            urlArr.push(addUrlValue);
            handleUrlSearch();
            addUrlValue = '';
        } else {
            notify('添加失败，' + res.statusText, 'error');
        }
        document.querySelector('#url').focus();
    };

    /**
     * url end----------------------
     */
</script>

<div class="container">
    <!-- 分类 -->
    <div>
        <!-- 分类搜索 -->
        <div class="header">
            <TextInput
                placeholder="搜索分类"
                bind:value={categorySearch}
                on:keydown={(event) => {
                    if (event.key === 'Enter') handleCategorySearch();
                }}
            />
            <Button kind="tertiary" on:click={handleCategorySearch}>搜索</Button
            >
            <Button kind="tertiary" on:click={() => (categoryModal = true)}
                >新增分类</Button
            >
        </div>
        <div>
            <!-- 分类列表 -->
            <StructuredList
                selection
                bind:selected={currentCategory}
                on:change={() => handleCategoryChange()}
            >
                <!-- 头部 -->
                <StructuredListHead>
                    <StructuredListRow head>
                        <StructuredListCell head>分类名称</StructuredListCell>
                        <StructuredListCell head>操作</StructuredListCell>
                        <StructuredListCell head>{''}</StructuredListCell>
                    </StructuredListRow>
                </StructuredListHead>
                <!-- 内容 -->
                <StructuredListBody>
                    <!-- 数据项 -->
                    {#each categories as item}
                        <StructuredListRow label for="row-{item}">
                            <StructuredListCell>
                                {item}
                            </StructuredListCell>
                            <StructuredListCell size="small">
                                <Button
                                    kind="danger"
                                    size="small"
                                    on:click={() => handleCategoryDelete(item)}
                                >
                                    删除
                                </Button>
                                <Button
                                    size="small"
                                    on:click={() => handleCategoryCopy(item)}
                                >
                                    复制
                                </Button>
                            </StructuredListCell>
                            <StructuredListInput
                                id="row-{item}"
                                value={item}
                                title="row-{item}-title"
                                name="row-{item}-name"
                            />
                            <StructuredListCell>
                                <CheckmarkFilled
                                    class="bx--structured-list-svg"
                                    aria-label="select an option"
                                    title="select an option"
                                />
                            </StructuredListCell>
                        </StructuredListRow>
                    {/each}
                </StructuredListBody>
            </StructuredList>
        </div>
    </div>
    <!-- URL -->
    <div>
        <!-- URL搜索 -->
        <div class="header">
            <TextInput
                bind:value={urlSearch}
                placeholder="搜索URL"
                on:keydown={(event) => {
                    if (event.key === 'Enter') handleUrlSearch();
                }}
            />
            <Button kind="tertiary" on:click={handleUrlSearch}>搜索</Button>
            <Button
                kind="tertiary"
                disabled={!currentCategory}
                on:click={() => (urlModal = true)}>新增URL</Button
            >
        </div>
        <div>
            <!-- URL列表 -->
            <StructuredList>
                <!-- 头部 -->
                <StructuredListHead>
                    <StructuredListRow head>
                        <StructuredListCell head>URL</StructuredListCell>
                        <StructuredListCell head>操作</StructuredListCell>
                    </StructuredListRow>
                </StructuredListHead>
                <!-- 内容 -->
                {#if urls && urls.length > 0}
                    <StructuredListBody>
                        <!-- 数据项 -->
                        {#each urls as item}
                            <StructuredListRow>
                                <StructuredListCell>
                                    {item}
                                </StructuredListCell>
                                <StructuredListCell size="small">
                                    <Button
                                        kind="danger"
                                        size="small"
                                        on:click={() => handleUrlDelete(item)}
                                    >
                                        删除
                                    </Button>
                                </StructuredListCell>
                            </StructuredListRow>
                        {/each}
                    </StructuredListBody>
                {/if}
            </StructuredList>
        </div>
    </div>
    <!-- 提示 -->
    <Tips />
    <!-- 分类添加弹窗 -->
    <Modal
        bind:open={categoryModal}
        modalHeading="添加URL分类"
        primaryButtonText="保存"
        secondaryButtonText="取消"
        selectorPrimaryFocus="#category"
        on:click:button--secondary={() => (categoryModal = false)}
        on:open
        on:close={() => {
            addCategoryValue = '';
        }}
        on:submit={handleCategorySubmit}
    >
        <TextInput
            id="category"
            invalid={!!categoryInvalidText}
            invalidText={categoryInvalidText}
            labelText="分类名称"
            placeholder="请输入分类名称"
            bind:value={addCategoryValue}
        />
    </Modal>

    <!-- URL添加弹窗 -->
    <Modal
        bind:open={urlModal}
        modalHeading="添加URL"
        primaryButtonText="保存"
        secondaryButtonText="取消"
        selectorPrimaryFocus="#url"
        on:click:button--secondary={() => (urlModal = false)}
        on:open
        on:close={() => {
            addUrlValue = '';
        }}
        on:submit={handleUrlSubmit}
    >
        <TextInput
            id="url"
            invalid={!!urlInvalidText}
            invalidText={urlInvalidText}
            labelText="URL"
            placeholder="请输入URL"
            bind:value={addUrlValue}
        />
    </Modal>
</div>

<style>
    .container {
        box-sizing: border-box;
        height: 100%;
        padding: 50px;
        display: flex;
        justify-content: center;
        gap: 20px;
    }
    .container > div {
        width: 50%;
    }
    .header {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 20px;
    }
</style>
