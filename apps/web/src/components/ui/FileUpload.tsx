/* eslint-disable @typescript-eslint/no-unused-vars */
interface FileUploadProps {
	label: string;
	onUpload: (file: File) => Promise<void>;
	accept?: string;
	disabled?: boolean;
	error?: string;
	required?: boolean;
}

export const FileUpload = ({
	label,
	onUpload,
	accept = "image/*",
	disabled = false,
	error,
	required = false,
}: FileUploadProps) => {
	const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
		const file = e.target.files?.[0];
		if (file) {
			try {
				await onUpload(file);
			} catch (error) {
				// Error handling is done at the parent component level
				e.target.value = ""; // Reset input after error
			}
		}
	};

	return (
		<div>
			<label className="block text-sm font-medium leading-6 text-likeme-text">
				{label}
				{required && <span className="text-red-500">*</span>}
			</label>
			<input
				type="file"
				onChange={handleFileChange}
				accept={accept}
				disabled={disabled}
				required={required}
				className="mt-2 block w-full text-sm text-gray-500
            file:mr-4 file:py-2 file:px-4
            file:rounded-md file:border-0
            file:text-sm file:font-semibold
            file:bg-likeme-primary file:text-white
            hover:file:bg-orange-900
            disabled:opacity-50 disabled:cursor-not-allowed"
			/>
			{error && <p className="mt-1 text-sm text-red-500">{error}</p>}
		</div>
	);
};
