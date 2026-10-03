/* eslint-disable @typescript-eslint/no-unused-vars */
"use client";
import React, { useState } from "react";
import { MotionProps, motion } from "framer-motion";
import { twMerge } from "tailwind-merge";
import { FiArrowRight, FiMail, FiMapPin } from "react-icons/fi";
import { AiFillLike } from "react-icons/ai";
import { RiUserFollowFill } from "react-icons/ri";
import { FaComment, FaShare } from "react-icons/fa";
import { toast } from "react-toastify";

export const ContactGrid = () => {
	return (
		<div className="min-h-full px-4 py-6 text-zinc-50">
			<motion.div
				initial="initial"
				animate="animate"
				transition={{
					staggerChildren: 0.05,
				}}
				className="mx-auto grid max-w-4xl grid-flow-dense grid-cols-12 gap-4"
			>
				<HeaderBlock />
				<SocialsBlock />
				<AboutBlock />
				<LocationBlock />
				<EmailListBlock />
			</motion.div>
		</div>
	);
};

type BlockProps = {
	className?: string;
} & MotionProps;

const Block = ({ className, ...rest }: BlockProps) => {
	return (
		<motion.div
			variants={{
				initial: {
					scale: 0.5,
					y: 50,
					opacity: 0,
				},
				animate: {
					scale: 1,
					y: 0,
					opacity: 1,
				},
			}}
			transition={{
				type: "spring",
				mass: 3,
				stiffness: 400,
				damping: 50,
			}}
			className={twMerge(
				"col-span-4 rounded-lg border border-likeme-accent bg-likeme-accent p-6",
				className
			)}
			{...rest}
		/>
	);
};

const HeaderBlock = () => (
	<Block className="col-span-12 row-span-2 md:col-span-6">
		<img
			src="https://api.dicebear.com/8.x/lorelei-neutral/svg?seed=John"
			alt="avatar"
			className="mb-4 size-14 rounded-full"
		/>
		<h1 className="mb-12 text-4xl font-medium leading-tight">
			Hi, we are LikeMe.{" "}
			<span className="text-likeme-text">We make people famous.</span>
		</h1>
		<a
			href="#"
			className="flex items-center gap-1 text-likeme-secondary hover:underline"
		>
			Contact us <FiArrowRight />
		</a>
	</Block>
);

const SocialsBlock = () => (
	<>
		<Block
			whileHover={{
				rotate: "2.5deg",
				scale: 1.1,
			}}
			className="col-span-6 bg-rose-500 md:col-span-3"
		>
			<span className="grid h-full place-content-center text-5xl text-white">
				<AiFillLike />
			</span>
		</Block>
		<Block
			whileHover={{
				rotate: "-2.5deg",
				scale: 1.1,
			}}
			className="col-span-6 bg-green-600 md:col-span-3"
		>
			<span className="grid h-full place-content-center text-5xl text-white">
				<RiUserFollowFill />
			</span>
		</Block>
		<Block
			whileHover={{
				rotate: "-2.5deg",
				scale: 1.1,
			}}
			className="col-span-6 bg-zinc-50 md:col-span-3"
		>
			<span className="grid h-full place-content-center text-5xl text-likeme-text">
				<FaComment />
			</span>
		</Block>
		<Block
			whileHover={{
				rotate: "2.5deg",
				scale: 1.1,
			}}
			className="col-span-6 bg-blue-500 md:col-span-3"
		>
			<span className="grid h-full place-content-center text-5xl text-white">
				<FaShare />
			</span>
		</Block>
	</>
);

const AboutBlock = () => (
	<Block className="col-span-12 text-3xl leading-snug">
		<p className="text-white">
			If you are an influencer and want to become part of our growing
			platform, we invite you to join our community.{" "}
			<span className="text-likeme-text">
				By collaborating with us, you can showcase your unique talents
				and reach a wider audience. To get started,{" "}
				<a href="/influencers/apply" className="underline">
					apply here
				</a>{" "}
				and become part of something special.
			</span>
		</p>
	</Block>
);

const LocationBlock = () => (
	<Block className="col-span-12 flex flex-col items-center gap-4 md:col-span-5">
		<FiMapPin className="text-3xl" />
		<p className="text-center text-lg text-white">Eindhoven, NL</p>
	</Block>
);

const EmailListBlock = () => {
	const [isCopied, setIsCopied] = useState(false);

	const handleCopyEmail = () => {
		navigator.clipboard.writeText("info@likeme.app").then(() => {
			setIsCopied(true);
			toast.success("Email has been copied to clipboard!"); 
		});
	};

	return (
		<Block className="col-span-12 md:col-span-7">
			<p className="mb-3 text-lg text-white">
				Perhaps you have other inquiries?
			</p>
			<form
				onSubmit={(e) => e.preventDefault()}
				className="flex items-center gap-2"
			>
				<button
					className="flex w-full items-center justify-center gap-2 whitespace-nowrap rounded bg-zinc-50 px-3 py-2 text-sm font-medium text-zinc-900 transition-colors hover:bg-zinc-300"
					onClick={handleCopyEmail} 
				>
					<FiMail /> Email Us
				</button>
			</form>
		</Block>
	);
};
