<template>
	<cl-crud ref="Crud">
		<cl-row>
			<cl-refresh-btn />

			<el-button
				v-permission="service.base.sys.log.permission.clear"
				type="danger"
				@click="clear"
			>
				{{ $t('清空') }}
			</el-button>


			<cl-flex1 />
			<cl-search-key :placeholder="$t('搜索请求地址、用户昵称、ip')" />
		</cl-row>

		<cl-row>
			<cl-table ref="Table" />
		</cl-row>

		<cl-row>
			<cl-flex1 />
			<cl-pagination />
		</cl-row>
	</cl-crud>
</template>

<script lang="ts" setup>
defineOptions({
	name: 'sys-log'
});

import { onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useCool } from '/@/cool';
import { useCrud, useTable } from '@cool-vue/crud';
import { useI18n } from 'vue-i18n';

const { service } = useCool();
const { t } = useI18n();


// cl-crud
const Crud = useCrud({ service: service.base.sys.log }, app => {
	app.refresh();
});

// cl-table
const Table = useTable({
	contextMenu: ['refresh'],
	columns: [
		{
			type: 'index',
			label: '#',
			width: 60
		},
		{
			prop: 'userId',
			label: t('用户ID'),
			minWidth: 100
		},
		{
			prop: 'name',
			label: t('用户昵称'),
			minWidth: 120
		},
		{
			prop: 'action',
			label: t('请求地址'),
			minWidth: 200,
			showOverflowTooltip: true
		},
		{
			prop: 'params',
			label: t('参数'),
			minWidth: 200,
			component: {
				name: 'cl-code-json',
				props: {
					popover: true
				}
			}
		},
		{
			prop: 'ip',
			label: 'ip',
			minWidth: 150,
			dict: [],
			dictColor: true,
			formatter(row) {
				return row.ip.split(',');
			}
		},
		{
			prop: 'createTime',
			label: t('请求时间'),
			minWidth: 170,
			sortable: 'desc'
		}
	]
});


// 清空日志
function clear() {
	ElMessageBox.confirm(t('是否要清空日志？'), t('提示'), {
		type: 'warning'
	})
		.then(() => {
			service.base.sys.log
				.clear()
				.then(() => {
					ElMessage.success(t('清空成功'));
					Crud.value?.refresh();
				})
				.catch(err => {
					ElMessage.error(err.message);
				});
		})
		.catch(() => null);
}

onMounted(() => {
});
</script>
