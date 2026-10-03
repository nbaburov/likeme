"use client";

import React, { useState } from "react";
import { FiChevronDown } from "react-icons/fi";
import { motion } from "framer-motion";
import useMeasure from "react-use-measure";

const ClientFAQ = () => {
	return (
		<div className="px-4 py-12">
			<div className="mx-auto max-w-3xl">
				<h3 className="mb-4 text-center text-3xl font-semibold">
					Frequently asked questions
				</h3>
				<Question title="How does LikeMe work?" defaultOpen>
					<span>
						We have a wide range of influencers on our platform, so
						you can find the perfect fit for your needs. We properly
						vet the influencers and approve them before they are
						added to our platform.
					</span>
				</Question>
				<Question title="How do I pay for the exposure I want?">
					<span>
						You can pay for the exposure you want using our secure
						payment gateway. We accept all major credit cards and
						debit cards.
					</span>
				</Question>
				<Question title="How do I know if the influencer is real?">
					<span>
						All of our influencers are real and have a verified
						Instagram account. We also have a strict verification
						process to ensure that the influencers are real and
						have a verified Instagram account.
					</span>
				</Question>
				<Question title="How fast do orders complete?">
					<span>
						The moment after you pay, the influencer will start
						working on your order. You will receive a notification
						when the order is complete. Max 3 days for the order to
						be completed.
					</span>
				</Question>
			</div>
		</div>
	);
};

const Question = ({
	title,
	children,
	defaultOpen = false,
}: {
	title: string;
	children: JSX.Element;
	defaultOpen?: boolean;
}) => {
	const [ref, { height }] = useMeasure();
	const [open, setOpen] = useState(defaultOpen);

	return (
		<motion.div
			animate={open ? "open" : "closed"}
			className="border-b-[1px] border-b-slate-300"
		>
			<button
				onClick={() => setOpen((pv) => !pv)}
				className="flex w-full items-center justify-between gap-4 py-6"
			>
				<motion.span
					variants={{
						open: {
							color: "rgba(3, 6, 23, 0)",
						},
						closed: {
							color: "rgba(3, 6, 23, 1)",
						},
					}}
					className="bg-gradient-to-r from-orange-600 to-rose-600 bg-clip-text text-left text-lg font-medium"
				>
					{title}
				</motion.span>
				<motion.span
					variants={{
						open: {
							rotate: "180deg",
							color: "rgb(124 58 237)",
						},
						closed: {
							rotate: "0deg",
							color: "#030617",
						},
					}}
				>
					<FiChevronDown className="text-2xl" />
				</motion.span>
			</button>
			<motion.div
				initial={false}
				animate={{
					height: open ? height : "0px",
					marginBottom: open ? "24px" : "0px",
				}}
				className="overflow-hidden text-slate-600"
			>
				<p ref={ref}>{children}</p>
			</motion.div>
		</motion.div>
	);
};

export default ClientFAQ;
