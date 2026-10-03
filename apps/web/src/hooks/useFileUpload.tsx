/* eslint-disable @typescript-eslint/no-explicit-any */
import { useMutation } from "@tanstack/react-query";
import { axiosInstance, publicAxiosInstance } from "@/lib/axios";
import { FileUploadResponseDTO } from "../dto/FileDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";

// Protected file operations
export const useFileUpload = () => {
	const { showError } = useErrorToast();

	const deleteMutation = useMutation<void, ApiError, string>({
		mutationFn: async (fileName) => {
			try {
				await axiosInstance.delete(`/files/${fileName}`);
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_DELETE_FILE",
					"Failed to delete file",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			toast.success("File deleted successfully");
		},
		onError: showError,
	});

	return {
		// Mutations
		delete: deleteMutation.mutateAsync,

		// Mutation states
		isDeleting: deleteMutation.isPending,

		// Mutation errors
		deleteError: deleteMutation.error,
	};
};

// Public file operations
export const usePublicFileUpload = () => {
	const { showError } = useErrorToast();

	const uploadMutation = useMutation<
		FileUploadResponseDTO,
		ApiError,
		{ file: File; prefix: string }
	>({
		mutationFn: async ({ file, prefix }) => {
			try {
				const formData = new FormData();
				formData.append("file", file);

				const { data } =
					await publicAxiosInstance.post<FileUploadResponseDTO>(
						`/files?prefix=${prefix}`,
						formData,
						{
							headers: {
								"Content-Type": "multipart/form-data",
							},
						}
					);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_UPLOAD_FILE",
					"Failed to upload file",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			toast.success("File uploaded successfully");
		},
		onError: showError,
	});

	return {
		// Mutations
		upload: uploadMutation.mutateAsync,

		// Mutation states
		isUploading: uploadMutation.isPending,

		// Mutation errors
		uploadError: uploadMutation.error,
	};
};
