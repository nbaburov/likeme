"use client";
import React from "react";
import { Typography } from "@material-tailwind/react";
import { FlagIcon } from "@heroicons/react/24/solid";
import BubbleButton from "@components/ui/BubbleButton";
import { useRouter } from "next/navigation";

export function ErrorSection7() {
	const router = useRouter();
	return (
		<div className="h-screen flex flex-col items-center justify-center text-center px-8">
			<div>
				<FlagIcon className="w-20 h-20 mx-auto" />
				<Typography
					variant="h1"
					color="default"
					className="mt-10 !text-3xl !leading-snug md:!text-4xl"
				>
					Error 404 <br /> It looks like something went wrong.
				</Typography>
				<Typography className="mt-8 mb-14 text-[18px] font-normal text-gray-500 mx-auto md:max-w-sm">
					Don&apos;t worry, our team is already on it.Please try
					refreshing the page or come back later.
				</Typography>
				<BubbleButton
					className="mx-auto"
					onClick={() => router.push("/")}
				>
					Back to Home
				</BubbleButton>
			</div>
		</div>
	);
}

export default ErrorSection7;
