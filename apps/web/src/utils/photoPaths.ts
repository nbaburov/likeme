export const getPhotoPath = (path: string) => {
	if (path.startsWith("http")) {
		return path;
	}
	return `${process.env.NEXT_PUBLIC_API_URL}/files/${path}`;
};
