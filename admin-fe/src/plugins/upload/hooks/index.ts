import { ElMessage } from 'element-plus';
import { module } from '/@/cool';
import { useI18n } from 'vue-i18n';

export function useUpload() {
	const { options } = module.get('upload');
	const { t } = useI18n();

	// 上传（功能已停用，对应后端接口已被删除）
	async function toUpload(file: File, opts: Upload.Options = {}): Upload.Response {
		return new Promise((_, reject) => {
			const msg = t('文件上传功能已停用');
			ElMessage.error(msg);
			reject(new Error(msg));
		});
	}

	return {
		options,
		toUpload
	};
}
