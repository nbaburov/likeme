import { Typography } from "@material-tailwind/react";

function InfoPageLayout({
	title,
	children,
}: {
	title: string;
	children: React.ReactNode;
}) {
	return (
		<div>
			<Typography
				className="text-center m-auto text-likeme-text mt-36 mb-4 capitalize"
				type="h1"
			>
				{title}
			</Typography>
			<div className="bg-white rounded-xl shadow-lg m-12 mt-4 md:m-24 p-2 md:p-4">
				{children}
			</div>
		</div>
	);
}

export default InfoPageLayout;
